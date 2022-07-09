package net.ncplanner.plannerator.multiblock;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.MenuEdit;
import net.ncplanner.plannerator.planner.gui.menu.MenuResize;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentEditorGrid;
import net.ncplanner.plannerator.planner.vr.VRGUI;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuResize;
public abstract class CuboidalMultiblock<T extends Block> extends Multiblock<T>{
    protected int x;
    protected int y;
    protected int z;
    private boolean casingPending = false;
    public CuboidalMultiblock(Configuration configuration, int x, int y, int z){
        super(configuration, x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public Multiblock<T> newInstance(Configuration configuration, int... dimensions){
        return newInstance(configuration, dimensions[0],dimensions[1],dimensions[2]);
    }
    public abstract Multiblock<T> newInstance(Configuration configuration, int x, int y, int z);
    @Override
    protected void createBlockGrids(){
        blockGrids.add(new BlockGrid(0, 0, 0, dimensions[0]+1, dimensions[1]+1, dimensions[2]+1));//cuz variables aren't set yet
    }
    @Override
    public void getEditorSpaces(ArrayList<EditorSpace<T>> editorSpaces){
        editorSpaces.add(new EditorSpace<T>(0, 0, 0, x+1, y+1, z+1){
            @Override
            public boolean isSpaceValid(T block, int x, int y, int z){
                if(block==null)return true;
                boolean x0 = x==0;
                boolean y0 = y==0;
                boolean z0 = z==0;
                boolean x1 = x==CuboidalMultiblock.this.x+1;
                boolean y1 = y==CuboidalMultiblock.this.y+1;
                boolean z1 = z==CuboidalMultiblock.this.z+1;
                if(x0||y0||z0||x1||y1||z1){
                    if(x0&&y0||x0&&z0||x0&&x1||x0&&y1||x0&&z1||y0&&z0||y0&&x1||y0&&y1||y0&&z1||z0&&x1||z0&&y1||z0&&z1||x1&&y1||x1&&z1||y1&&z1){
                        return canBePlacedInCasingEdge(block);
                    }else return canBePlacedInCasingFace(block);
                }
                return canBePlacedWithinCasing(block);
            }
            @Override
            public void createComponents(MenuEdit editor, ArrayList<Component> comps, int cellSize){
                for(int y = 0; y<=CuboidalMultiblock.this.y+1; y++){
                    comps.add(new MenuComponentEditorGrid(0, 0, cellSize, editor, CuboidalMultiblock.this, this, 0, 0, CuboidalMultiblock.this.x+1, CuboidalMultiblock.this.z+1, Axis.Y, y));
                }
            }
        });
    }
    public abstract boolean canBePlacedWithinCasing(T b);
    public abstract boolean canBePlacedInCasingEdge(T b);
    public abstract boolean canBePlacedInCasingFace(T b);
    public abstract int getMinX();
    public abstract int getMinY();
    public abstract int getMinZ();
    public abstract int getMaxX();
    public abstract int getMaxY();
    public abstract int getMaxZ();
    public int getInternalWidth(){
        return x;
    }
    public int getInternalHeight(){
        return y;
    }
    public int getInternalDepth(){
        return z;
    }
    public int getExternalWidth(){
        return x+2;
    }
    public int getExternalHeight(){
        return y+2;
    }
    public int getExternalDepth(){
        return z+2;
    }
    public boolean isCompact(int totalBlocks){
        int blockCount = getBlocks(true).size();
        int volume = getExternalDepth()*getExternalHeight()*getExternalWidth();
        int bitsPerDim = MathUtil.logBase(2, Math.max(getExternalWidth(), Math.max(getExternalHeight(), getExternalDepth())));
        int bitsPerType = MathUtil.logBase(2, totalBlocks);
        int compactBits = bitsPerType*volume;
        int spaciousBits = 4*Math.max(bitsPerDim, bitsPerType)*blockCount;
        return compactBits<spaciousBits;
    }
    public void forEachInternalPosition(BlockPosConsumer func){
        for(int x = 1; x<=getInternalWidth(); x++){
            for(int y = 1; y<=getInternalHeight(); y++){
                for(int z = 1; z<=getInternalDepth(); z++){
                    func.accept(x, y, z);
                }
            }
        }
    }
    public void forEachCasingPosition(BlockPosConsumer func){
        forEachPosition((x, y, z) -> {
            boolean x0 = x==0;
            boolean y0 = y==0;
            boolean z0 = z==0;
            boolean x1 = x==this.x+1;
            boolean y1 = y==this.y+1;
            boolean z1 = z==this.z+1;
            if(x0||y0||z0||x1||y1||z1){
                func.accept(x, y, z);
            }
        });
    }
    public void forEachCasingEdgePosition(BlockPosConsumer func){
        forEachPosition((x, y, z) -> {
            boolean x0 = x==0;
            boolean y0 = y==0;
            boolean z0 = z==0;
            boolean x1 = x==this.x+1;
            boolean y1 = y==this.y+1;
            boolean z1 = z==this.z+1;
            if(x0||y0||z0||x1||y1||z1){
                if(x0&&y0||x0&&z0||x0&&x1||x0&&y1||x0&&z1||y0&&z0||y0&&x1||y0&&y1||y0&&z1||z0&&x1||z0&&y1||z0&&z1||x1&&y1||x1&&z1||y1&&z1){
                    func.accept(x, y, z);
                }
            }
        });
    }
    public void forEachCasingFacePosition(BlockPosConsumer func){
        forEachPosition((x, y, z) -> {
            boolean x0 = x==0;
            boolean y0 = y==0;
            boolean z0 = z==0;
            boolean x1 = x==this.x+1;
            boolean y1 = y==this.y+1;
            boolean z1 = z==this.z+1;
            if(x0||y0||z0||x1||y1||z1){
                if(x0&&y0||x0&&z0||x0&&x1||x0&&y1||x0&&z1||y0&&z0||y0&&x1||y0&&y1||y0&&z1||z0&&x1||z0&&y1||z0&&z1||x1&&y1||x1&&z1||y1&&z1){
                }else{
                    func.accept(x, y, z);
                }
            }
        });
    }
    public int getInternalVolume(){
        return getInternalWidth()*getInternalHeight()*getInternalDepth();
    }
    public void expandRight(int i){
        if(getInternalWidth()+i>getMaxX())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[0]+=i;
        x+=i;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            T block = cache.get(pos);
            int bx = pos.x;
            int by = pos.y;
            int bz = pos.z;
            if(bx==x-i+1)bx+=i;
            setBlock(bx, by, bz, block);
        };
        history.clear();
        future.clear();
        clearCaches();
    }
    public void expandLeft(int i){
        if(getInternalWidth()+i>getMaxX())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[0]+=i;
        x+=i;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            T block = cache.get(pos);
            int bx = pos.x;
            int by = pos.y;
            int bz = pos.z;
            if(bx>0)bx+=i;
            setBlock(bx, by, bz, block);
        };
        history.clear();
        future.clear();
        clearCaches();
    }
    public void expandUp(int i){
        if(getInternalHeight()+i>getMaxY())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[1]+=i;
        y+=i;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            T block = cache.get(pos);
            int bx = pos.x;
            int by = pos.y;
            int bz = pos.z;
            if(by==y-i+1)by+=i;
            setBlock(bx, by, bz, block);
        };
        history.clear();
        future.clear();
        clearCaches();
    }
    public void exandDown(int i){
        if(getInternalHeight()+i>getMaxY())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[1]+=i;
        y+=i;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            T block = cache.get(pos);
            int bx = pos.x;
            int by = pos.y;
            int bz = pos.z;
            if(by>0)by+=i;
            setBlock(bx, by, bz, block);
        };
        history.clear();
        future.clear();
        clearCaches();
    }
    public void expandToward(int i){
        if(getInternalDepth()+i>getMaxZ())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[2]+=i;
        z+=i;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            T block = cache.get(pos);
            int bx = pos.x;
            int by = pos.y;
            int bz = pos.z;
            if(bz==z-i+1)bz+=i;
            setBlock(bx, by, bz, block);
        };
        history.clear();
        future.clear();
        clearCaches();
    }
    public void expandAway(int i){
        if(getInternalDepth()+i>getMaxZ())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[2]+=i;
        z+=i;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            T block = cache.get(pos);
            int bx = pos.x;
            int by = pos.y;
            int bz = pos.z;
            if(bz>0)bz+=i;
            setBlock(bx, by, bz, block);
        };
        history.clear();
        future.clear();
        clearCaches();
    }
    public void deleteX(int X){
        if(getInternalWidth()<=getMinX())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[0]--;
        x--;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            T block = cache.get(pos);
            int bx = pos.x;
            int by = pos.y;
            int bz = pos.z;
            if(bx==X+1)continue;
            if(bx>X+1)bx--;
            setBlock(bx, by, bz, block);
        };
        history.clear();
        future.clear();
        clearCaches();
    }
    public void deleteY(int Y){
        if(getInternalHeight()<=getMinY())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[1]--;
        y--;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            T block = cache.get(pos);
            int bx = pos.x;
            int by = pos.y;
            int bz = pos.z;
            if(by==Y+1)continue;
            if(by>Y+1)by--;
            setBlock(bx, by, bz, block);
        };
        history.clear();
        future.clear();
        clearCaches();
    }
    public void deleteZ(int Z){
        if(getInternalDepth()<=getMinZ())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[2]--;
        z--;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            T block = cache.get(pos);
            int bx = pos.x;
            int by = pos.y;
            int bz = pos.z;
            if(bz==Z+1)continue;
            if(bz>Z+1)bz--;
            setBlock(bx, by, bz, block);
        };
        history.clear();
        future.clear();
        clearCaches();
    }
    public void insertX(int X){
        if(getInternalWidth()>=getMaxX())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[0]++;
        x++;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            T block = cache.get(pos);
            int bx = pos.x;
            int by = pos.y;
            int bz = pos.z;
            if(bx>X)bx++;
            setBlock(bx, by, bz, block);
        };
        history.clear();
        future.clear();
        clearCaches();
    }
    public void insertY(int Y){
        if(getInternalHeight()>=getMaxY())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[1]++;
        y++;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            T block = cache.get(pos);
            int bx = pos.x;
            int by = pos.y;
            int bz = pos.z;
            if(by>Y)by++;
            setBlock(bx, by, bz, block);
        };
        history.clear();
        future.clear();
        clearCaches();
    }
    public void insertZ(int Z){
        if(getInternalDepth()>=getMaxZ())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[2]++;
        z++;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            T block = cache.get(pos);
            int bx = pos.x;
            int by = pos.y;
            int bz = pos.z;
            if(bz>Z)bz++;
            setBlock(bx, by, bz, block);
        }
        history.clear();
        future.clear();
        clearCaches();
    }
    @Override
    public String getDimensionsStr(){
        return getInternalWidth()+"x"+getInternalHeight()+"x"+getInternalDepth();
    }
    @Override
    public Menu getResizeMenu(GUI gui, MenuEdit editor){
        return new MenuResize(gui, editor, this);
    }
    @Override
    public void openVRResizeMenu(VRGUI gui, VRMenuEdit editor){
        gui.open(new VRMenuResize(gui, editor, this));
    }
    private HashMap<BlockPos, T> cache(){
        HashMap<BlockPos, T> cache = new HashMap<>();
        forEachPosition((x, y, z) -> {
            cache.put(new BlockPos(x,y,z), getBlock(x, y, z));
        });
        return cache;
    }
    @Override
    public void convertTo(Configuration to) throws MissingConfigurationEntryException{
        doConvertTo(to);
        if(casingPending)buildDefaultCasing();
        casingPending = false;
    }
    public abstract void doConvertTo(Configuration to) throws MissingConfigurationEntryException;
    public void buildDefaultCasingOnConvert(){
        casingPending = true;
    }
    public abstract void buildDefaultCasing();
    @Override
    public void init(){
        super.init();
        buildDefaultCasing();
    }
    @Override
    public boolean shouldHideWithCasing(int x, int y, int z){
        return x==0||y==0||z==0||x==getExternalWidth()-1||y==getExternalHeight()-1||z==getExternalDepth()-1;
    }
    @Override
    public BoundingBox getBoundingBox(boolean includeCasing){
        BoundingBox bbox = super.getBoundingBox(includeCasing);
        if(!includeCasing){
            bbox = new BoundingBox(bbox.x1+1, bbox.y1+1, bbox.z1+1, bbox.x2-1, bbox.y2-1, bbox.z2-1);
        }
        return bbox;
    }
}