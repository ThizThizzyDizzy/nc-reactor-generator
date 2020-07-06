package multiblock;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.configuration.Configuration;
import simplelibrary.Stack;
import simplelibrary.config2.Config;
import simplelibrary.opengl.ImageStash;
public abstract class Multiblock<T extends Block> extends MultiblockBit{
    public Stack<Action> history = new Stack<>();
    public Stack<Action> future = new Stack<>();
    public HashMap<String, String> metadata = new HashMap<>();
    {
        resetMetadata();
    }
    public void resetMetadata(){
        metadata.clear();
        metadata.put("Name", "");
        metadata.put("Author", "");
    }
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
        updateBlockLocations();
        history.clear();
        future.clear();
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
        updateBlockLocations();
        history.clear();
        future.clear();
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
        updateBlockLocations();
        history.clear();
        future.clear();
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
        updateBlockLocations();
        history.clear();
        future.clear();
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
        updateBlockLocations();
        history.clear();
        future.clear();
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
        updateBlockLocations();
        history.clear();
        future.clear();
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
        updateBlockLocations();
        history.clear();
        future.clear();
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
        updateBlockLocations();
        history.clear();
        future.clear();
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
        updateBlockLocations();
        history.clear();
        future.clear();
    }
    public void clearData(List<T> blocks){
        for(T t : blocks){
            t.clearData();
        }
    }
    public abstract void calculate(List<T> blocks);
    private ArrayList<T> lastBlocks = null;
    private boolean forceRescan = false;
    public ArrayList<T> getBlocks(){
        if(lastBlocks!=null&&!forceRescan)return lastBlocks;
        lastBlocks = new ArrayList<>();
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    T b = getBlock(x, y, z);
                    if(b!=null)lastBlocks.add(b);
                }
            }
        }
        return lastBlocks;
    }
    protected abstract T newCasing(int x, int y, int z);
    public abstract String getTooltip();
    public void draw3D(){
        ArrayList<T> blocks = getBlocks();
        Collections.sort(blocks, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2){
                return o1.getName().compareTo(o2.getName());
            }
        });
        Block last = null;
        for(T block : blocks){
            if(last==null||last.getBaseTexture()!=block.getBaseTexture()){
                if(last!=null)GL11.glEnd();
                ImageStash.instance.bindTexture(Core.getTexture(block.getBaseTexture()));
                GL11.glBegin(GL11.GL_QUADS);
            }
            drawCube(block);
            last = block;
        }
        if(!blocks.isEmpty())GL11.glEnd();
    }
    public void draw3DInOrder(){
        ArrayList<T> blocks = getBlocks();
        Collections.sort(blocks, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2){
                if(o1.y!=o2.y)return o2.y-o1.y;
                int d1 = o1.x-o1.z;
                int d2 = o2.x-o2.z;
                return d1-d2;
            }
        });
        Block last = null;
        for(T block : blocks){
            if(last==null||last.getBaseTexture()!=block.getBaseTexture()){
                if(last!=null)GL11.glEnd();
                ImageStash.instance.bindTexture(Core.getTexture(block.getBaseTexture()));
                GL11.glBegin(GL11.GL_QUADS);
            }
            drawCubeInOrder(block);
            last = block;
        }
        if(!blocks.isEmpty())GL11.glEnd();
    }
    private void drawCube(T block){
        int x = block.x;
        int y = block.y;
        int z = block.z;
        //xy +z
        if(z==getZ()-1||getBlock(x, y, z+1)==null){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x, y, z+1);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x+1, y, z+1);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x+1, y+1, z+1);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x, y+1, z+1);
        }
        //xy -z
        if(z==0||getBlock(x,y,z-1)==null){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x, y, z);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x, y+1, z);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x+1, y+1, z);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x+1, y, z);
        }
        //xz +y
        if(y==getY()-1||getBlock(x, y+1, z)==null){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x, y+1, z);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x, y+1, z+1);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x+1, y+1, z+1);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x+1, y+1, z);
        }
        //xz -y
        if(y==0||getBlock(x, y-1, z)==null){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x, y, z);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x+1, y, z);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x+1, y, z+1);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x, y, z+1);
        }
        //yz +x
        if(x==getX()-1||getBlock(x+1, y, z)==null){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x+1, y, z);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x+1, y+1, z);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x+1, y+1, z+1);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x+1, y, z+1);
        }
        //yz -x
        if(x==0||getBlock(x-1, y, z)==null){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x, y, z);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x, y, z+1);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x, y+1, z+1);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x, y+1, z);
        }
    }
    private void drawCubeInOrder(T block){
        int x = block.x;
        int y = block.y;
        int z = block.z;
//        //xy +z
//        if(z==getZ()-1||getBlock(x, y, z+1)==null){
//            GL11.glTexCoord2d(0, 0);
//            GL11.glVertex3d(x, y, z+1);
//            GL11.glTexCoord2d(0, 1);
//            GL11.glVertex3d(x+1, y, z+1);
//            GL11.glTexCoord2d(1, 1);
//            GL11.glVertex3d(x+1, y+1, z+1);
//            GL11.glTexCoord2d(1, 0);
//            GL11.glVertex3d(x, y+1, z+1);
//        }
        //xy -z
        if(z==0||getBlock(x,y,z-1)==null){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x, y, z);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x, y+1, z);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x+1, y+1, z);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x+1, y, z);
        }
//        //xz +y
//        if(y==getY()-1||getBlock(x, y+1, z)==null){
//            GL11.glTexCoord2d(0, 0);
//            GL11.glVertex3d(x, y+1, z);
//            GL11.glTexCoord2d(0, 1);
//            GL11.glVertex3d(x, y+1, z+1);
//            GL11.glTexCoord2d(1, 1);
//            GL11.glVertex3d(x+1, y+1, z+1);
//            GL11.glTexCoord2d(1, 0);
//            GL11.glVertex3d(x+1, y+1, z);
//        }
        //xz -y
        if(y==0||getBlock(x, y-1, z)==null){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x, y, z);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x+1, y, z);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x+1, y, z+1);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x, y, z+1);
        }
        //yz +x
        if(x==getX()-1||getBlock(x+1, y, z)==null){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x+1, y, z);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x+1, y+1, z);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x+1, y+1, z+1);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x+1, y, z+1);
        }
//        //yz -x
//        if(x==0||getBlock(x-1, y, z)==null){
//            GL11.glTexCoord2d(0, 0);
//            GL11.glVertex3d(x, y, z);
//            GL11.glTexCoord2d(0, 1);
//            GL11.glVertex3d(x, y, z+1);
//            GL11.glTexCoord2d(1, 1);
//            GL11.glVertex3d(x, y+1, z+1);
//            GL11.glTexCoord2d(1, 0);
//            GL11.glVertex3d(x, y+1, z);
//        }
    }
    public final void save(Configuration configuration, OutputStream stream){
        Config config = Config.newConfig();
        config.set("id", getMultiblockID());
        Config meta = Config.newConfig();
        for(String key : metadata.keySet()){
            String value = metadata.get(key);
            if(value.trim().isEmpty())continue;
            meta.set(key,value);
        }
        if(meta.properties().length>0){
            config.set("metadata", meta);
        }
        save(configuration, config);
        config.save(stream);
    }
    protected abstract void save(Configuration configuration, Config config);
    public abstract int getMultiblockID();
    public abstract void convertTo(Configuration to);
    /**
     * @return true if anything changed
     */
    public abstract boolean validate();
    private void updateBlockLocations(){
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    T b = getBlock(x, y, z);
                    if(b==null)continue;
                    b.x = x;
                    b.y = y;
                    b.z = z;
                }
            }
        }
    }
    public String getName(){
        return metadata.containsKey("Name")?metadata.get("Name"):"";
    }
    public abstract boolean exists();
    public void undo(){
        if(!history.isEmpty()){
            Action a = history.pop();
            recalculate(a.undo(this));
            future.push(a);
        }
    }
    public void redo(){
        if(!future.isEmpty()){
            Action a = future.pop();
            recalculate(a.apply(this));
            history.push(a);
        }
    }
    public void action(Action action){
        recalculate(action.apply(this));
        future.clear();
        history.push(action);
    }
    public void recalculate(){
        List<T> blox = getBlocks();
        clearData(blox);
        validate();
        calculate(blox);
    }
    private void recalculate(ActionResult result){
        forceRescan = true;
        List<T> blox = result.getAffectedGroups();
        clearData(blox);
        if(validate()){
            recalculate();
            return;
        }
        calculate(blox);
        forceRescan = false;
    }
    public ArrayList<T> getGroup(T block){
        ArrayList<T> group = new ArrayList<>();
        if(block==null)return group;
        if(!block.canGroup())return null;
        group.add(block);
        boolean somethingChanged;
        do{
            somethingChanged = false;
            for(T blok : getBlocks()){
                if(group.contains(blok))continue;
                boolean req = false;
                for(T b : group){
                    if(b.requires(blok, this)||blok.requires(b, this))req = true;
                }
                if(req){
                    group.add(blok);
                    somethingChanged = true;
                }
            }
        }while(somethingChanged);
        return group;
    }
    public ArrayList<T> getAffectedGroups(List<T> blocks){
        ArrayList<T> group = new ArrayList<>();
        for(T block : blocks){
            ArrayList<T> g = getGroup(block);
            if(g==null)return getBlocks();
            for(T b : g){
                if(!group.contains(b))group.add(b);
            }
        }
        return group;
    }
    public boolean isEmpty(){
        return getBlocks().isEmpty();
    }
    public void setBlock(int x, int y, int z, Block block){
        blocks[x][y][z] = block==null?null:block.copy(x, y, z);
    }
    public String getSaveTooltip(){
        String s = Core.configuration.name+" ("+Core.configuration.version+")\n";
        for(String key : metadata.keySet()){
            if(key.equalsIgnoreCase("name")){
                s+=metadata.get(key)+"\n";
            }
        }
        s+=getDefinitionName()+"\n";
        for(String key : metadata.keySet()){
            if(key.equalsIgnoreCase("name"))continue;
            if(metadata.get(key)!=null)s+=key+": "+metadata.get(key)+"\n";
        }
        s+="\n";
        return s+getTooltip();
    }
}