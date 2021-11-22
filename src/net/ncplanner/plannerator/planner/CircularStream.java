package net.ncplanner.plannerator.planner;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * CircularStream class from SimpleLibraryPlus. (https://github.com/ThizThizzyDizzy/SimpleLibraryPlus/blob/master/src/simplelibrary/Queue.java
 */
public class CircularStream extends OutputStream{
    private byte[] buffer;
    private int dataStart = 0;//First valid byte
    private int dataEnd = 0;//First INVALID byte
    private boolean isEmpty = true;
    private int maxBufferSize;
    private boolean closed = false;
    private CircularStreamInput in = new CircularStreamInput();
    public CircularStream(int maxBufferSize){
        this.maxBufferSize = maxBufferSize;
        buffer = new byte[maxBufferSize];
    }
    @Override
    public void write(int b){
        synchronized(buffer){
            while(getBufferSize()>=maxBufferSize&&!closed) try{ buffer.wait(); }catch(InterruptedException ex){}
            if(closed) return;
            if(dataEnd==buffer.length) dataEnd = 0;
            buffer[dataEnd]=(byte)b;
            dataEnd++;
            isEmpty = false;
            buffer.notifyAll();
        }
    }
    private int getBufferSize(){
        if(dataEnd==dataStart) return isEmpty?0:maxBufferSize;
        else if(dataEnd>dataStart) return dataEnd-dataStart;
        else return dataEnd-dataStart+maxBufferSize;//if dataEnd<dataStart- buffer wraps around the backing array.
    }
    @Override
    public void write(byte[] b, int offset, int length){
        synchronized(buffer){
            while(length>0&&!closed){
                while(getBufferSize()>=maxBufferSize&&!closed) try{ buffer.wait(); }catch(InterruptedException ex){}
                if(closed) return;
                if(dataEnd==buffer.length) dataEnd = 0;
                int canWrite = Math.min(maxBufferSize-dataEnd, maxBufferSize-getBufferSize());
                int toWrite = Math.min(canWrite, length);
                System.arraycopy(b, offset, buffer, dataEnd, toWrite);
                dataEnd+=toWrite;
                offset+=toWrite;
                length-=toWrite;
                isEmpty = false;
                buffer.notifyAll();
            }
        }
    }
    public CircularStreamInput getInput(){
        return in;
    }
    public void close(){
        closed = true;
        synchronized(buffer){ buffer.notifyAll(); }
    }
    public class CircularStreamInput extends InputStream{
        @Override
        public int read(){
            synchronized(buffer){
                while(getBufferSize()<1&&!closed) try{ buffer.wait(); }catch(InterruptedException ex){}
                if(closed&&getBufferSize()<1) return -1;//EOF
                if(dataStart==buffer.length) dataStart = 0;
                byte data = buffer[dataStart];
                dataStart++;
                if(dataStart==dataEnd) isEmpty=true;//Start just moved to match end
                buffer.notifyAll();
                return ((int)data+0x100)%0x100;//Convert number ranged -128 to +127 to number ranged 0-255
            }
        }
        @Override
        public int available(){
            return getBufferSize();
        }
        @Override
        public int read(byte[] data, int offset, int length){
            synchronized(buffer){
                int read = 0;
                while(read<1||(length>0&&getBufferSize()>0)){
                    while(getBufferSize()<1&&!closed) try{ buffer.wait(); }catch(InterruptedException ex){}
                    if(closed&&getBufferSize()<1) return read>0?read:-1;//EOF
                    if(dataStart==buffer.length) dataStart = 0;
                    int canRead = Math.min(maxBufferSize-dataStart, getBufferSize());
                    int toRead = Math.min(canRead, length);
                    System.arraycopy(buffer, dataStart, data, offset, toRead);
                    dataStart+=toRead;
                    offset+=toRead;
                    length-=toRead;
                    read+=toRead;
                    if(dataStart==dataEnd) isEmpty = true;
                    buffer.notifyAll();
                }
                return read;
            }
        }
        @Override
        public void close(){
            CircularStream.this.close();
        }
    }
}
