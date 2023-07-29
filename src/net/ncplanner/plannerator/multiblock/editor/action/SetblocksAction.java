package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.Symmetry;
import net.ncplanner.plannerator.multiblock.editor.Action;
public class SetblocksAction extends Action<Multiblock>{
    public final HashSet<BlockPos> locations = new HashSet<>();
    public final AbstractBlock block;
    private final HashMap<BlockPos, AbstractBlock> was = new HashMap<>();
    public SetblocksAction(AbstractBlock block){
        this.block = block;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        for(BlockPos loc : locations){
            if(allowUndo)was.put(loc, multiblock.getBlock(loc.x, loc.y, loc.z));
            multiblock.setBlock(loc.x, loc.y, loc.z, block);
        }
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(BlockPos loc : was.keySet()){
            multiblock.setBlockExact(loc.x, loc.y, loc.z, was.get(loc));
        }
    }
    public SetblocksAction add(int x, int y, int z){
        locations.add(new BlockPos(x,y,z));
        return this;
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<AbstractBlock> blocks){
        for(BlockPos loc : locations){
            AbstractBlock b = multiblock.getBlock(loc.x, loc.y, loc.z);
            if(b!=null)blocks.add(b);
        }
    }
    public boolean isEmpty(){
        return locations.isEmpty();
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof SetblocksAction){
            SetblocksAction other = (SetblocksAction)obj;
            if(block==null&&other.block!=null)return false;
            if(block==null||block.isEqual(other.block)){
                return locations.equals(other.locations);
            }
        }
        return false;
    }
    public void symmetrize(Multiblock multiblock, Symmetry symmetry){
        ArrayList<BlockPos> newLocs = new ArrayList<>();
        BoundingBox bbox = multiblock.getBoundingBox();
        locations.forEach((t) -> {
            symmetry.apply(t.x, t.y, t.z, bbox.getWidth(), bbox.getHeight(), bbox.getDepth(), (x, y, z) -> {
                newLocs.add(new BlockPos(x,y,z));
            });
        });
        locations.addAll(newLocs);
    }
}