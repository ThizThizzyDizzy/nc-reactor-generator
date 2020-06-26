package planner.multiblock;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import planner.Core;
import simplelibrary.Queue;
import simplelibrary.opengl.ImageStash;
public abstract class Multiblock<T extends Block> extends MultiblockBit{
    public Block[][][] blocks;
    public Multiblock(int x, int y, int z){
        blocks = new Block[x][y][z];
    }
    public T getBlock(int x, int y, int z){
        if(x<0||y<0||z<0||x>=getX()||y>=getY()||z>=getZ())return newCasing(x,y,z);
        return (T) blocks[x][y][z];
    }
    public abstract String getDefinitionName();
    public abstract Multiblock<T> newInstance();
    public abstract void getAvailableBlocks(List<T> blocks);
    public final List<T> getAvailableBlocks(){
        ArrayList<T> list = new ArrayList<>();
        getAvailableBlocks(list);
        return list;
    }
    public int getX(){
        return blocks.length;
    }
    public int getY(){
        return blocks[0].length;
    }
    public int getZ(){
        return blocks[0][0].length;
    }
    public abstract int getMinSize();
    public abstract int getMaxSize();
    public void expandRight(int i){
        if(getX()+i>getMaxSize())return;
        Block[][][] blks = new Block[getX()+i][getY()][getZ()];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][y][z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
    }
    public void expandLeft(int i){
        if(getX()+i>getMaxSize())return;
        Block[][][] blks = new Block[getX()+i][getY()][getZ()];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x+i][y][z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
    }
    public void expandUp(int i){
        if(getY()+i>getMaxSize())return;
        Block[][][] blks = new Block[getX()][getY()+i][getZ()];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][y][z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
    }
    public void exandDown(int i){
        if(getY()+i>getMaxSize())return;
        Block[][][] blks = new Block[getX()][getY()+i][getZ()];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][y+i][z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
    }
    public void expandToward(int i){
        if(getZ()+i>getMaxSize())return;
        Block[][][] blks = new Block[getX()][getY()][getZ()+i];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][y][z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
    }
    public void expandAway(int i){
        if(getZ()+i>getMaxSize())return;
        Block[][][] blks = new Block[getX()][getY()][getZ()+i];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][y][z+i] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
    }
    public void deleteX(int X){
        if(getX()<=getMinSize())return;
        Block[][][] blks = new Block[getX()-1][getY()][getZ()];
        for(int x = 0; x<blks.length; x++){
            for(int y = 0; y<blks[x].length; y++){
                for(int z = 0; z<blks[x][y].length; z++){
                    blks[x][y][z] = blocks[(x>=X?1:0)+x][y][z];
                }
            }
        }
        blocks = blks;
    }
    public void deleteY(int Y){
        if(getY()<=getMinSize())return;
        Block[][][] blks = new Block[getX()][getY()-1][getZ()];
        for(int x = 0; x<blks.length; x++){
            for(int y = 0; y<blks[x].length; y++){
                for(int z = 0; z<blks[x][y].length; z++){
                    blks[x][y][z] = blocks[x][(y>=Y?1:0)+y][z];
                }
            }
        }
        blocks = blks;
    }
    public void deleteZ(int Z){
        if(getZ()<=getMinSize())return;
        Block[][][] blks = new Block[getX()][getY()][getZ()-1];
        for(int x = 0; x<blks.length; x++){
            for(int y = 0; y<blks[x].length; y++){
                for(int z = 0; z<blks[x][y].length; z++){
                    blks[x][y][z] = blocks[x][y][(z>=Z?1:0)+z];
                }
            }
        }
        blocks = blks;
    }
    public void clearData(){
        for(T t : getBlocks()){
            t.clearData();
        }
    }
    public abstract void calculate();
    public Queue<T> getBlocks(){
        Queue<T> blox = new Queue<>();
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    T b = getBlock(x, y, z);
                    if(b!=null)blox.enqueue(b);
                }
            }
        }
        return blox;
    }
    protected abstract T newCasing(int x, int y, int z);
    public abstract String getTooltip();
    public void draw3D(){
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    drawCube(x, y, z, getBlock(x, y, z));
                }
            }
        }
    }
    private void drawCube(int x, int y, int z, T block){
        if(block==null)return;
        ImageStash.instance.bindTexture(Core.getTexture(block.getBaseTexture()));
        GL11.glBegin(GL11.GL_QUADS);
        //xy +z
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex3d(x, y, z+1);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex3d(x+1, y, z+1);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex3d(x+1, y+1, z+1);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex3d(x, y+1, z+1);
        //xy -z
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex3d(x, y, z);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex3d(x, y+1, z);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex3d(x+1, y+1, z);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex3d(x+1, y, z);
        //xz +y
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex3d(x, y+1, z);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex3d(x, y+1, z+1);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex3d(x+1, y+1, z+1);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex3d(x+1, y+1, z);
        //xz -y
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex3d(x, y, z);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex3d(x+1, y, z);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex3d(x+1, y, z+1);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex3d(x, y, z+1);
        //yz +x
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex3d(x+1, y, z);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex3d(x+1, y+1, z);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex3d(x+1, y+1, z+1);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex3d(x+1, y, z+1);
        //yz -x
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex3d(x, y, z);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex3d(x, y, z+1);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex3d(x, y+1, z+1);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex3d(x, y+1, z);
        GL11.glEnd();
    }
}