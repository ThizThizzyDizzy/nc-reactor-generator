package multiblock;
import multiblock.ppe.PostProcessingEffect;
import generator.Priority;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import multiblock.action.SetblockAction;
import multiblock.configuration.Block;
import multiblock.symmetry.Symmetry;
import org.lwjgl.opengl.GL11;
import planner.Core;
import multiblock.configuration.Configuration;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentMinimaList;
import simplelibrary.Queue;
import simplelibrary.Stack;
import simplelibrary.config2.Config;
import simplelibrary.opengl.ImageStash;
public abstract class Multiblock<T extends Block> extends MultiblockBit{
    public long lastChangeTime;
    public Stack<Action> history = new Stack<>();
    public Stack<Action> future = new Stack<>();
    public Queue<Action> queue = new Queue<>();
    public HashMap<String, String> metadata = new HashMap<>();
    public Boolean showDetails = null;//details override
    private Configuration configuration;
    {
        resetMetadata();
        lastChangeTime = System.nanoTime();
    }
    public void resetMetadata(){
        metadata.clear();
        metadata.put("Name", "");
        metadata.put("Author", "");
    }
    protected Block[][][] blocks;
    public Multiblock(int x, int y, int z){
        blocks = new Block[x][y][z];
    }
    public T getBlock(int x, int y, int z){
        if(x<0||y<0||z<0||x>=getX()||y>=getY()||z>=getZ())return getCasing();
        return (T) blocks[x][y][z];
    }
    public abstract String getDefinitionName();
    public abstract Multiblock<T> newInstance(Configuration c);
    public final Multiblock<T> newInstance(){
        return newInstance(Core.configuration);
    }
    public abstract Multiblock<T> newInstance(int x, int y, int z);
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
    public int getDisplayZ(){
        return getZ();
    }
    public abstract int getMinX();
    public abstract int getMinY();
    public abstract int getMinZ();
    public abstract int getMaxX();
    public abstract int getMaxY();
    public abstract int getMaxZ();
    public void expandRight(int i){
        if(getX()+i>getMaxX())return;
        Block[][][] blks = new Block[getX()+i][getY()][getZ()];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][y][z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
        history.clear();
        future.clear();
    }
    public void expandLeft(int i){
        if(getX()+i>getMaxX())return;
        Block[][][] blks = new Block[getX()+i][getY()][getZ()];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x+i][y][z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
        history.clear();
        future.clear();
    }
    public void expandUp(int i){
        if(getY()+i>getMaxY())return;
        Block[][][] blks = new Block[getX()][getY()+i][getZ()];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][y][z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
        history.clear();
        future.clear();
    }
    public void exandDown(int i){
        if(getY()+i>getMaxY())return;
        Block[][][] blks = new Block[getX()][getY()+i][getZ()];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][y+i][z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
        history.clear();
        future.clear();
    }
    public void expandToward(int i){
        if(getZ()+i>getMaxZ())return;
        Block[][][] blks = new Block[getX()][getY()][getZ()+i];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][y][z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
        history.clear();
        future.clear();
    }
    public void expandAway(int i){
        if(getZ()+i>getMaxZ())return;
        Block[][][] blks = new Block[getX()][getY()][getZ()+i];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][y][z+i] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
        history.clear();
        future.clear();
    }
    public void deleteX(int X){
        if(getX()<=getMinX())return;
        Block[][][] blks = new Block[getX()-1][getY()][getZ()];
        for(int x = 0; x<blks.length; x++){
            for(int y = 0; y<blks[x].length; y++){
                for(int z = 0; z<blks[x][y].length; z++){
                    blks[x][y][z] = blocks[(x>=X?1:0)+x][y][z];
                }
            }
        }
        blocks = blks;
        history.clear();
        future.clear();
    }
    public void deleteY(int Y){
        if(getY()<=getMinY())return;
        Block[][][] blks = new Block[getX()][getY()-1][getZ()];
        for(int x = 0; x<blks.length; x++){
            for(int y = 0; y<blks[x].length; y++){
                for(int z = 0; z<blks[x][y].length; z++){
                    blks[x][y][z] = blocks[x][(y>=Y?1:0)+y][z];
                }
            }
        }
        blocks = blks;
        history.clear();
        future.clear();
    }
    public void deleteZ(int Z){
        if(getZ()<=getMinZ())return;
        Block[][][] blks = new Block[getX()][getY()][getZ()-1];
        for(int x = 0; x<blks.length; x++){
            for(int y = 0; y<blks[x].length; y++){
                for(int z = 0; z<blks[x][y].length; z++){
                    blks[x][y][z] = blocks[x][y][(z>=Z?1:0)+z];
                }
            }
        }
        blocks = blks;
        history.clear();
        future.clear();
    }
    public void insertX(int X){
        if(getX()>=getMaxX())return;
        Block[][][] blks = new Block[getX()+1][getY()][getZ()];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[(x>=X?1:0)+x][y][z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
        history.clear();
        future.clear();
    }
    public void insertY(int Y){
        if(getY()>=getMaxY())return;
        Block[][][] blks = new Block[getX()][getY()+1][getZ()];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][(y>=Y?1:0)+y][z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
        history.clear();
        future.clear();
    }
    public void insertZ(int Z){
        if(getZ()>=getMaxZ())return;
        Block[][][] blks = new Block[getX()][getY()][getZ()+1];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][y][(z>=Z?1:0)+z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
        history.clear();
        future.clear();
    }
    public abstract void clearData();
    public abstract void calculate();
    public ArrayList<BlockHolder<T>> getAbsoluteBlocks(){
        ArrayList<BlockHolder<T>> absolute = new ArrayList<>();
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    T b = getBlock(x, y, z);
                    if(b!=null){
                        absolute.add(new BlockHolder<>(x,y,z,b));
                    }
                }
            }
        }
        return absolute;
    }
    protected abstract T getCasing();
    public abstract String getTooltip();
    public void draw3D(){
        ArrayList<BlockHolder<T>> holders = getAbsoluteBlocks();
        Collections.sort(holders, (BlockHolder<T> o1, BlockHolder<T> o2) -> o1.block.getName().compareTo(o2.block.getName()));
        BlockHolder<T> last = null;
        for(BlockHolder<T> holder : holders){
            if(last==null||last.getBaseTexture()!=holder.getBaseTexture()){
                if(last!=null)GL11.glEnd();
                ImageStash.instance.bindTexture(Core.getTexture(holder.getBaseTexture()));
                GL11.glBegin(GL11.GL_QUADS);
            }
            drawCube(holder, false);
            last = holder;
        }
        if(!holders.isEmpty())GL11.glEnd();
    }
    public void draw3DInOrder(){
        ArrayList<BlockHolder<T>> holders = getAbsoluteBlocks();
        Collections.sort(holders, (BlockHolder<T> o1, BlockHolder<T> o2) -> {
            if(o1.y!=o2.y)return o2.y-o1.y;
            int d1 = o1.x-o1.z;
            int d2 = o2.x-o2.z;
            return d1-d2;
        });
        BlockHolder<T> last = null;
        for(BlockHolder<T> holder : holders){
            if(last==null||last.getBaseTexture()!=holder.getBaseTexture()){
                if(last!=null)GL11.glEnd();
                ImageStash.instance.bindTexture(Core.getTexture(holder.getBaseTexture()));
                GL11.glBegin(GL11.GL_QUADS);
            }
            drawCube(holder, true);
            last = holder;
        }
        if(!holders.isEmpty())GL11.glEnd();
    }
    protected void drawCube(BlockHolder<T> holder, boolean inOrder){
        int x = holder.x;
        int y = holder.y;
        int z = holder.z;
        //xy +z
        if(!inOrder&&(z==getZ()-1||getBlock(x, y, z+1)==null)){
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
        if(!inOrder&&(y==getY()-1||getBlock(x, y+1, z)==null)){
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
        if(!inOrder&&(x==0||getBlock(x-1, y, z)==null)){
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
    public final void save(NCPFFile ncpf, Configuration configuration, OutputStream stream){
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
        save(ncpf, configuration, config);
        config.save(stream);
    }
    protected void save(NCPFFile ncpf, Configuration configuration, Config config){}
    public abstract int getMultiblockID();
    public abstract void convertTo(Configuration to);
    /**
     * @return true if anything changed
     */
    public abstract boolean validate();
    public String getName(){
        return metadata.containsKey("Name")?metadata.get("Name"):"";
    }
    public abstract boolean exists();
    public void undo(){
        if(!history.isEmpty()){
            Action a = history.pop();
            a.undo(this);
            recalculate();
            future.push(a);
        }
    }
    public void redo(){
        if(!future.isEmpty()){
            Action a = future.pop();
            a.apply(this, true);
            recalculate();
            history.push(a);
        }
    }
    public void action(Action action, boolean allowUndo){
        lastChangeTime = System.nanoTime();
        action.apply(this, allowUndo);
        recalculate();
        future.clear();
        if(allowUndo)history.push(action);
    }
    public void recalculate(){
        clearData();
        validate();
        calculate();
    }
    public boolean isEmpty(){
        for(int x = 0; x<getX(); x++){
            for(int y =0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    if(getBlock(x, y, z)!=null)return false;
                }
            }
        }
        return true;
    }
    public void setBlock(int x, int y, int z, Block block){
        blocks[x][y][z] = block;
    }
    public String getSaveTooltip(){
        String s = Core.configuration.name+" ("+(getDefinitionName().contains("Underhaul")?Core.configuration.underhaulVersion:Core.configuration.overhaulVersion)+")\n";
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
        String extra = getExtraSaveTooltip();
        return extra.isEmpty()?(s+getTooltip()):(s+getExtraSaveTooltip()+"\n"+getTooltip());
    }
    public String getBotTooltip(){
        String s = Core.configuration.name+" ("+(getDefinitionName().contains("Underhaul")?Core.configuration.underhaulVersion:Core.configuration.overhaulVersion)+")\n";
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
        return s+getExtraBotTooltip();
    }
    protected String getExtraSaveTooltip(){
        return "";
    }
    protected abstract String getExtraBotTooltip();
    public abstract void addGeneratorSettings(MenuComponentMinimaList multiblockSettings);
    public ArrayList<Priority> getGenerationPriorities(){
        ArrayList<Priority> priorities = new ArrayList<>();
        getGenerationPriorities(priorities);
        return priorities;
    }
    public abstract void getGenerationPriorities(ArrayList<Priority> priorities);
    public ArrayList<Priority.Preset> getGenerationPriorityPresets(){
        return getGenerationPriorityPresets(getGenerationPriorities());
    }
    public ArrayList<Priority.Preset> getGenerationPriorityPresets(ArrayList<Priority> priorities){
        ArrayList<Priority.Preset> presets = new ArrayList<>();
        getGenerationPriorityPresets(priorities, presets);
        return presets;
    }
    public abstract void getGenerationPriorityPresets(ArrayList<Priority> priorities, ArrayList<Priority.Preset> presets);
    public ArrayList<Symmetry> getSymmetries(){
        ArrayList<Symmetry> symmetries = new ArrayList<>();
        getSymmetries(symmetries);
        return symmetries;
    }
    public abstract void getSymmetries(ArrayList<Symmetry> symmetries);
    public ArrayList<PostProcessingEffect> getPostProcessingEffects(){
        ArrayList<PostProcessingEffect> postProcessingEffects = new ArrayList<>();
        getPostProcessingEffects(postProcessingEffects);
        return postProcessingEffects;
    }
    public abstract void getPostProcessingEffects(ArrayList<PostProcessingEffect> postProcessingEffects);
    public int getVolume(){
        return getX()*getY()*getZ();
    }
    public void queueAction(Action action){
        queue.enqueue(action);
    }
    public void performActions(boolean allowUndo){
        future.clear();
        while(!queue.isEmpty()){
            Action action = queue.dequeue();
            action.apply(this, allowUndo);
            history.push(action);
        }
        recalculate();
    }
    public boolean isBetterThan(Multiblock other, ArrayList<Priority> priorities){
        for(Priority p : priorities){
            double result = p.compare(this, other);
            if(result>0)return true;
            if(result<0)return false;
        }
        return false;
    }
    public boolean isCoreBetterThan(Multiblock other, ArrayList<Priority> priorities){
        for(Priority p : priorities){
            if(!p.isCore())continue;
            double result = p.compare(this, other);
            if(result>0)return true;
            if(result<0)return false;
        }
        return false;
    }
    public abstract Multiblock<T> blankCopy();
    public abstract Multiblock<T> copy();
    public long nanosSinceLastChange(){
        return System.nanoTime()-lastChangeTime;
    }
    public long millisSinceLastChange(){
        return nanosSinceLastChange()/1_000_000;
    }
    public int count(Object o){
        if(o==null){
            int total = 0;
            for(int x = 0; x<getX(); x++){
                for(int y = 0; y<getY(); y++){
                    for(int z = 0; z<getZ(); z++){
                        Block block = getBlock(x, y, z);
                        for(Action a : queue){
                            if(a instanceof SetblockAction){
                                SetblockAction set = (SetblockAction)a;
                                if(set.x==x&&set.y==y&&set.z==z)block = set.block;
                            }
                        }
                        if(block==null)total++;
                    }
                }
            }
            return total;
        }
        if(o instanceof Block){
            return getBlocks((T)o);
        }
        return doCount(o);
    }
    protected abstract int doCount(Object o);
    public int getBlocks(T type){
        int total = 0;
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block block = getBlock(x, y, z);
                    for(Action a : queue){
                        if(a instanceof SetblockAction){
                            SetblockAction set = (SetblockAction)a;
                            if(set.x==x&&set.y==y&&set.z==z)block = set.block;
                        }
                    }
                    if(block==null)continue;
                    if(block==type)total++;
                }
            }
        }
        return total;
    }
    public abstract String getGeneralName();
    protected abstract boolean isCompatible(Multiblock<T> other);
    public boolean checkCompatible(Multiblock<T> other){
        if(other.getX()!=getX())return false;
        if(other.getY()!=getY())return false;
        if(other.getZ()!=getZ())return false;
        return isCompatible(other);
    }
    public void setConfiguration(Configuration configuration){
        this.configuration = configuration;
    }
    public Configuration getConfiguration(){
        if(configuration==null)return Core.configuration;
        return configuration;
    }
    public final HashMap<String, Double> getFluidOutputs(){
        HashMap<String, Double> outputs = new HashMap<>();
        getFluidOutputs(outputs);
        return outputs;
    }
    protected abstract void getFluidOutputs(HashMap<String, Double> outputs);
    public final ArrayList<PartCount> getPartsList(){
        ArrayList<PartCount> parts = new ArrayList<>();
        getMainParts(parts);
        getExtraParts(parts);
        Collections.sort(parts);
        return parts;
    }
    protected void getMainParts(ArrayList<PartCount> parts){
        HashMap<T, Integer> blocks = new HashMap<>();
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    T block = getBlock(x, y, z);
                    if(blocks.containsKey(block))blocks.put(block, blocks.get(block)+1);
                    else blocks.put(block, 1);
                }
            }
        }
        for(T block : blocks.keySet()){
            parts.add(new PartCount(block.getTexture(), block.getName(), blocks.get(block)));
        }
    }
    protected abstract void getExtraParts(ArrayList<PartCount> parts);
    public boolean isValid(Block block, int x, int y, int z){
        return block.hasRules()&&block.calculateRules(x, y, z, this);
    }
    public abstract String getDescriptionTooltip();
    public Queue<int[]> getAdjacent(int x, int y, int z){
        Queue<int[]> adjacent = new Queue<>();
        for(Direction d : directions){
            T b = getBlock(x+d.x, y+d.y, z+d.z);
            if(b!=null)adjacent.enqueue(new int[]{x+d.x,y+d.y,z+d.z});
        }
        return adjacent;
    }
    public Queue<int[]> getActiveAdjacent(int x, int y, int z){
        Queue<int[]> adjacent = new Queue<>();
        for(Direction d : directions){
            T b = getBlock(x+d.x, y+d.y, z+d.z);
            if(b!=null&&isActive(x,y,z))adjacent.enqueue(new int[]{x+d.x,y+d.y,z+d.z});
        }
        return adjacent;
    }
    public Queue<T> getAdjacentBlocks(int x, int y, int z){
        Queue<T> adjacent = new Queue<>();
        for(Direction d : directions){
            T b = getBlock(x+d.x, y+d.y, z+d.z);
            if(b!=null)adjacent.enqueue(b);
        }
        return adjacent;
    }
    public Queue<T> getActiveAdjacentBlocks(int x, int y, int z){
        Queue<T> adjacent = new Queue<>();
        for(Direction d : directions){
            T b = getBlock(x+d.x, y+d.y, z+d.z);
            if(b!=null&&isActive(x,y,z))adjacent.enqueue(b);
        }
        return adjacent;
    }
    public abstract boolean isActive(int x, int y, int z);
}