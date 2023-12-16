package net.ncplanner.plannerator.planner.dssl;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.planner.dssl.token.Token;
public class DSSLProcessor{
    private DSSLInterpreter interpreter;
    private Thread processorThread = null;
    private final Consumer<String> print;
    private final Consumer<String> debug;
    private final Supplier<String> read;
    public DSSLProcessor(Consumer<String> print, Consumer<String> debug, Supplier<String> read){
        this.print = print;
        this.debug = debug;
        this.read = read;
    }
    /**
     * Reinitialize processor with a specified script
     * @param file 
     */
    public void init(File file){
        if(processorThread!=null)throw new IllegalStateException("Cannot reinitialize while the processor is running!");
        try{
            interpreter = new DSSLInterpreter(file, print, debug, read);
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    /**
     * Starts the script thread.
     */
    public void start(int steps){
        if(interpreter==null)throw new IllegalStateException("Processor has not been initialized!");
        if(processorThread!=null)throw new IllegalStateException("Cannot run while the processor is running!");
        processorThread = new Thread(() -> {
            RuntimeException e = null;
            long tim = System.nanoTime();
            try{
                interpreter.start(steps);
            }catch(Exception ex){
                e = new RuntimeException(ex);
            }
            debug.accept("\nTime: "+(System.nanoTime()-tim)/1000000+"ms");
            finishProcessing();
            if(e!=null)throw(e);
        });
        processorThread.start();
    }
    public void run(){
        if(interpreter==null)throw new IllegalStateException("Processor has not been initialized!");
        if(processorThread==null)start(-1);
        else interpreter.run();
    }
    public void step(){
        if(interpreter==null)throw new IllegalStateException("Processor has not been initialized!");
        if(processorThread==null)start(1);
        else interpreter.step();
    }
    /**
     * @return true if any script is loaded, running, or paused in the debugger.
     */
    public boolean isActive(){
        return interpreter!=null;
    }
    /**
     * @return true if the current script is actively processing.
     */
    public boolean isRunning(){
        return processorThread!=null&&interpreter.runSteps!=0;
    }
    public void halt(){
        if(interpreter!=null)interpreter.halt();
    }
    private void finishProcessing(){
        processorThread = null;
        if(interpreter.isFinished())interpreter = null;
    }
    public void setBreakpoints(HashSet<Integer> breakpoints){
        if(interpreter==null)throw new IllegalStateException("Processor has not been initialized!");
        interpreter.breakpoints = breakpoints;
    }
    public Token getCurrentToken(){
        if(interpreter==null)return null;
        return interpreter.currentToken;
    }
}