package net.ncplanner.plannerator.multiblock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.MenuEdit;
import net.ncplanner.plannerator.planner.gui.menu.MenuResize;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentEditorGrid;
import net.ncplanner.plannerator.planner.vr.VRGUI;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuResize;
public abstract class CuboidalMultiblock<T extends AbstractBlock> extends Multiblock<T>{
    protected int x;
    protected int y;
    protected int z;
    private boolean casingPending = false;
    public CuboidalMultiblock(NCPFConfigurationContainer configuration, int x, int y, int z){
        super(configuration, x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public Multiblock<T> newInstance(NCPFConfigurationContainer configuration, int... dimensions){
        return newInstance(configuration, dimensions[0],dimensions[1],dimensions[2]);
    }
    public abstract Multiblock<T> newInstance(NCPFConfigurationContainer configuration, int x, int y, int z);
    @Override
    protected void createBlockGrids(){
        blockGrids.add(new BlockGrid(0, 0, 0, dimensions[0]+1, dimensions[1]+1, dimensions[2]+1));//cuz variables aren't set yet
    }
    @Override
    public void getEditorSpaces(ArrayList<EditorSpace<T>> editorSpaces){
        editorSpaces.add(new EditorSpace<T>(0, 0, 0, x+1, y+1, z+1){
            @Override
            public boolean isSpaceValid(T block, BlockPos pos){
                if(block==null)return true;
                boolean x0 = pos.x==0;
                boolean y0 = pos.y==0;
                boolean z0 = pos.z==0;
                boolean x1 = pos.x==CuboidalMultiblock.this.x+1;
                boolean y1 = pos.y==CuboidalMultiblock.this.y+1;
                boolean z1 = pos.z==CuboidalMultiblock.this.z+1;
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
    public void forEachInternalPosition(Consumer<BlockPos> func){
        BoundingBox.around(new BlockPos(1,1,1), new BlockPos(getInternalWidth(), getInternalHeight(), getInternalDepth())).forEachPosition(func);
    }
    public void forEachCasingPosition(Consumer<BlockPos> func){
        forEachPosition((pos) -> {
            boolean x0 = pos.x==0;
            boolean y0 = pos.y==0;
            boolean z0 = pos.z==0;
            boolean x1 = pos.x==this.x+1;
            boolean y1 = pos.y==this.y+1;
            boolean z1 = pos.z==this.z+1;
            if(x0||y0||z0||x1||y1||z1){
                func.accept(pos);
            }
        });
    }
    public void forEachCasingEdgePosition(Consumer<BlockPos> func){
        forEachPosition((pos) -> {
            boolean x0 = pos.x==0;
            boolean y0 = pos.y==0;
            boolean z0 = pos.z==0;
            boolean x1 = pos.x==this.x+1;
            boolean y1 = pos.y==this.y+1;
            boolean z1 = pos.z==this.z+1;
            if(x0||y0||z0||x1||y1||z1){
                if(x0&&y0||x0&&z0||x0&&x1||x0&&y1||x0&&z1||y0&&z0||y0&&x1||y0&&y1||y0&&z1||z0&&x1||z0&&y1||z0&&z1||x1&&y1||x1&&z1||y1&&z1){
                    func.accept(pos);
                }
            }
        });
    }
    public void forEachCasingFacePosition(Consumer<BlockPos> func){
        forEachPosition((pos) -> {
            boolean x0 = pos.x==0;
            boolean y0 = pos.y==0;
            boolean z0 = pos.z==0;
            boolean x1 = pos.x==this.x+1;
            boolean y1 = pos.y==this.y+1;
            boolean z1 = pos.z==this.z+1;
            if(x0||y0||z0||x1||y1||z1){
                if(x0&&y0||x0&&z0||x0&&x1||x0&&y1||x0&&z1||y0&&z0||y0&&x1||y0&&y1||y0&&z1||z0&&x1||z0&&y1||z0&&z1||x1&&y1||x1&&z1||y1&&z1){
                }else{
                    func.accept(pos);
                }
            }
        });
    }
    public int getInternalVolume(){
        return getInternalWidth()*getInternalHeight()*getInternalDepth();
    }
    public void expand(int i, Direction direction){
        if(getInternalWidth()+i*Math.abs(direction.x)>getMaxX())return;
        if(getInternalHeight()+i*Math.abs(direction.y)>getMaxY())return;
        if(getInternalDepth()+i*Math.abs(direction.z)>getMaxZ())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[0]+=i*Math.abs(direction.x);
        dimensions[1]+=i*Math.abs(direction.y);
        dimensions[2]+=i*Math.abs(direction.z);
        x+=i*Math.abs(direction.x);
        y+=i*Math.abs(direction.y);
        z+=i*Math.abs(direction.z);
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            int ox = 0;
            int oy = 0;
            int oz = 0;
            if(direction.x<0)ox = pos.x>0?i:0;
            if(direction.x>0)ox = pos.x==x-i+1?i:0;
            if(direction.y<0)oy = pos.y>0?i:0;
            if(direction.y>0)oy = pos.y==y-i+1?i:0;
            if(direction.z<0)oz = pos.z>0?i:0;
            if(direction.z>0)oz = pos.z==z-i+1?i:0;
            setBlock(pos.offset(ox, oy, oz), cache.get(pos));
        }
        history.clear();
        future.clear();
        clearCaches();
    }
    public void delete(int deletePos, Axis axis){
        if(getInternalWidth()-axis.x<getMinX())return;
        if(getInternalHeight()-axis.y<getMinY())return;
        if(getInternalDepth()-axis.z<getMinZ())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[0]-=axis.x;
        dimensions[1]-=axis.y;
        dimensions[2]-=axis.z;
        x-=axis.x;
        y-=axis.y;
        z-=axis.z;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            int p = pos.x*axis.x+pos.y*axis.y+pos.z*axis.z;
            if(p==deletePos+1)continue;
            setBlock(pos.offset(p>deletePos+1?-axis.x:0, p>deletePos+1?-axis.y:0, p>deletePos+1?-axis.z:0), cache.get(pos));
        }
        history.clear();
        future.clear();
        clearCaches();
    }
    public void insert(int insertPos, Axis axis){
        if(getInternalWidth()+axis.x>getMaxX())return;
        if(getInternalHeight()+axis.y>getMaxY())return;
        if(getInternalDepth()+axis.z>getMaxZ())return;
        HashMap<BlockPos, T> cache = cache();
        blockGrids.clear();
        dimensions[0]+=axis.x;
        dimensions[1]+=axis.y;
        dimensions[2]+=axis.z;
        x+=axis.x;
        y+=axis.y;
        z+=axis.z;
        createBlockGrids();
        for(BlockPos pos : cache.keySet()){
            int p = pos.x*axis.x+pos.y*axis.y+pos.z*axis.z;
            setBlock(pos.offset(p>insertPos?axis.x:0, p>insertPos?axis.y:0, p>insertPos?axis.z:0), cache.get(pos));
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
        forEachPosition((pos) -> {
            cache.put(pos, getBlock(pos));
        });
        return cache;
    }
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
    public boolean shouldHideWithCasing(BlockPos pos){
        return pos.x==0||pos.y==0||pos.z==0||pos.x==getExternalWidth()-1||pos.y==getExternalHeight()-1||pos.z==getExternalDepth()-1;
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