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
import multiblock.symmetry.Symmetry;
import org.lwjgl.opengl.GL11;
import planner.Core;
import multiblock.configuration.Configuration;
import planner.FormattedText;
import planner.file.NCPFFile;
import planner.menu.MenuResize;
import planner.menu.component.MenuComponentMinimaList;
import planner.editor.module.Module;
import planner.editor.suggestion.Suggestor;
import planner.menu.MenuEdit;
import planner.vr.VRGUI;
import planner.vr.menu.VRMenuEdit;
import planner.vr.menu.VRMenuResize;
import simplelibrary.Queue;
import simplelibrary.Stack;
import simplelibrary.config2.Config;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.GUI;
public abstract class Multiblock<T extends Block> extends MultiblockBit{
    public long lastChangeTime;
    public Stack<Action> history = new Stack<>();
    public Stack<Action> future = new Stack<>();
    public Queue<Action> queue = new Queue<>();
    public HashMap<String, String> metadata = new HashMap<>();
    public Boolean showDetails = null;//details override
    public Configuration configuration;
    private boolean calculated = false;
    public HashMap<Module, Object> moduleData = new HashMap<Module, Object>();
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
    public Multiblock(Configuration configuration, int x, int y, int z){
        blocks = new Block[x][y][z];
        this.configuration = configuration;
    }
    public T getBlock(int x, int y, int z){
        if(x<0||y<0||z<0||x>=getX()||y>=getY()||z>=getZ())return newCasing(x,y,z);
        return (T) blocks[x][y][z];
    }
    public abstract String getDefinitionName();
    public abstract Multiblock<T> newInstance(Configuration configuration);
    public final Multiblock<T> newInstance(){
        return newInstance(Core.configuration);
    }
    public abstract Multiblock<T> newInstance(Configuration configuration, int x, int y, int z);
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
        updateBlockLocations();
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
        updateBlockLocations();
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
        updateBlockLocations();
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
        updateBlockLocations();
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
        updateBlockLocations();
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
        updateBlockLocations();
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
        updateBlockLocations();
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
        updateBlockLocations();
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
        updateBlockLocations();
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
        updateBlockLocations();
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
        updateBlockLocations();
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
        updateBlockLocations();
        history.clear();
        future.clear();
    }
    public void clearData(List<T> blocks){
        for(T t : blocks){
            t.clearData();
        }
        moduleData.clear();
    }
    public abstract void doCalculate(List<T> blocks);
    public void calculate(List<T> blocks){
        doCalculate(blocks);
        for(Module m : Core.modules){
            if(m.isActive()){
                Object result = m.calculateMultiblock(this);
                if(result!=null)moduleData.put(m, result);
            }
        }
        calculated = true;
    }
    private ArrayList<T> lastBlocks = null;
    private boolean forceRescan = false;
    public ArrayList<T> getAbsoluteBlocks(){
        ArrayList<T> absolute = new ArrayList<>();
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    T b = getBlock(x, y, z);
                    if(b!=null){
                        T t = (T)b.copy(x, y, z);
                        t.x = x;
                        t.y = y;
                        t.z = z;
                        //because blades
                        absolute.add(t);
                    }
                }
            }
        }
        return absolute;
    }
    public ArrayList<T> getBlocks(){
        return getBlocks(false);
    }
    public ArrayList<T> getBlocks(boolean rescan){
        if(lastBlocks!=null&&!rescan&&!forceRescan)return lastBlocks;
        ArrayList<T> lastBlox = new ArrayList<>();
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    T b = getBlock(x, y, z);
                    if(b!=null)lastBlox.add(b);
                }
            }
        }
        return lastBlocks = lastBlox;
    }
    protected abstract T newCasing(int x, int y, int z);
    public abstract FormattedText getTooltip();
    public String getModuleTooltip(){
        String s = "";
        for(Module m : moduleData.keySet()){
            String str = m.getTooltip(this, moduleData.get(m));
            if(str!=null)s+="\n"+str;
        }
        return s;
    }
    public void draw3D(){
        ArrayList<T> blocks = getAbsoluteBlocks();
        Collections.sort(blocks, (T o1, T o2) -> o1.getName().compareTo(o2.getName()));
        Block last = null;
        for(T block : blocks){
            if(last==null||last.getBaseTexture()!=block.getBaseTexture()){
                if(last!=null)GL11.glEnd();
                ImageStash.instance.bindTexture(Core.getTexture(block.getBaseTexture()));
                GL11.glBegin(GL11.GL_QUADS);
            }
            drawCube(block, false);
            last = block;
        }
        if(!blocks.isEmpty())GL11.glEnd();
    }
    public void draw3DInOrder(){
        ArrayList<T> blocks = getAbsoluteBlocks();
        Collections.sort(blocks, (T o1, T o2) -> {
            if(o1.y!=o2.y)return o2.y-o1.y;
            int d1 = o1.x-o1.z;
            int d2 = o2.x-o2.z;
            return d1-d2;
        });
        Block last = null;
        for(T block : blocks){
            if(last==null||last.getBaseTexture()!=block.getBaseTexture()){
                if(last!=null)GL11.glEnd();
                ImageStash.instance.bindTexture(Core.getTexture(block.getBaseTexture()));
                GL11.glBegin(GL11.GL_QUADS);
            }
            drawCube(block, true);
            last = block;
        }
        if(!blocks.isEmpty())GL11.glEnd();
    }
    protected void drawCube(T block, boolean inOrder){
        int x = block.x;
        int y = block.y;
        int z = block.z;
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
            recalculate(a.apply(this, true));
            history.push(a);
        }
    }
    public void action(Action action, boolean allowUndo){
        lastChangeTime = System.nanoTime();
        recalculate(action.apply(this, allowUndo));
        future.clear();
        if(allowUndo)history.push(action);
    }
    public void recalculate(){
        List<T> blox = getBlocks();
        clearData(blox);
        validate();
        calculate(blox);
    }
    private void recalculate(ActionResult result){
        forceRescan = true;
        ArrayList<T> affectedGroups = result.getAffectedGroups();
        recalculate(affectedGroups==null?getBlocks(true):affectedGroups);
        forceRescan = false;
    }
    private void recalculate(List<T> blox){
        forceRescan = true;
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
    public void setBlockExact(int x, int y, int z, Block exact){
        blocks[x][y][z] = exact;
    }
    public String getSaveTooltip(){
        String s = getConfiguration().name+" ("+(getDefinitionName().contains("Underhaul")?getConfiguration().underhaulVersion:getConfiguration().overhaulVersion)+")\n";
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
        String s = getConfiguration().name+" ("+(getDefinitionName().contains("Underhaul")?getConfiguration().underhaulVersion:getConfiguration().overhaulVersion)+")\n";
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
        ArrayList<T> affected = new ArrayList<>();
        while(!queue.isEmpty()){
            Action action = queue.dequeue();
            ActionResult result = action.apply(this, allowUndo);
            ArrayList<T> affectedGroups = result.getAffectedGroups();
            if(affected!=null){
                if(affectedGroups==null)affected = null;
                else affected.addAll(affectedGroups);
            }
            history.push(action);
        }
        if(affected==null){
            recalculate(getBlocks(true));
            return;
        }
        Set<T> actual = new HashSet<>();
        for(T t : affected){
            T b = getBlock(t.x, t.y, t.z);
            if(b==null)continue;
            actual.add(b);
        }
        recalculate(new ArrayList<>(actual));
    }
    public boolean isBetterThan(Multiblock other, ArrayList<Priority> priorities){
        for(Priority p : priorities){
            double result = p.compare(this, other);
            if(result>0)return true;
            if(result<0)return false;
        }
        return false;
    }
    public int compareTo(Multiblock other, ArrayList<Priority> priorities){
        for(Priority p : priorities){
            double result = p.compare(this, other);
            if(result>0)return 1;
            if(result<0)return -1;
        }
        return 0;
    }
    public abstract Multiblock<T> blankCopy();
    public Multiblock<T> copy(){
        Multiblock<T> copy = doCopy();
        copy.metadata = (HashMap<String, String>)metadata.clone();
        copy.moduleData = (HashMap<Module, Object>)moduleData.clone();
        copy.calculated = calculated;
        return copy;
    }
    public abstract Multiblock<T> doCopy();
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
                    if(block.isEqual(type))total++;
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
    public Configuration getConfiguration(){
        if(configuration==null)return Core.configuration;//TODO maybe force it to have a specific configuration?
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
        FOR:for(T block : getBlocks(true)){
            for(T t : blocks.keySet()){
                if(t.isEqual(block)){
                    blocks.put(t, blocks.get(t)+1);
                    continue FOR;
                }
            }
            blocks.put(block, 1);
        }
        for(T block : blocks.keySet()){
            parts.add(new PartCount(block.getTexture(), block.getName(), blocks.get(block)));
        }
    }
    protected abstract void getExtraParts(ArrayList<PartCount> parts);
    public boolean isValid(Block block, int x, int y, int z){
        Block b = block.newInstance(x, y, z);
        return b.hasRules()&&b.calculateRules(this);
    }
    public abstract String getDescriptionTooltip();
    public float get3DPreviewScale(){
        return 1;
    }
    @Deprecated
    public void openResizeMenu(GUI gui, MenuEdit editor){
        gui.open(new MenuResize(gui, editor, this));
    }
    @Deprecated
    public void openVRResizeMenu(VRGUI gui, VRMenuEdit editor){
        gui.open(new VRMenuResize(gui, editor, this));
    }
    public boolean areBlocksEqual(Multiblock other){
        if(getX()!=other.getX())return false;
        if(getY()!=other.getY())return false;
        if(getZ()!=other.getZ())return false;
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block a = getBlock(x, y, z);
                    Block b = other.getBlock(x, y, z);
                    if(a==b)continue;//all good
                    if(a==null&&b!=null)return false;
                    if(a!=null&&b==null)return false;
                    if(!a.isEqual(b))return false;
                }
            }
        }
        return true;
    }
    public ArrayList<Suggestor> getSuggestors(){
        ArrayList<Suggestor> suggestors = new ArrayList<>();
        getSuggestors(suggestors);
        return suggestors;
    }
    public abstract void getSuggestors(ArrayList<Suggestor> suggestors);
    public boolean isCalculated(){
        return calculated;
    }
}