package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.symmetry.Symmetry;
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
            if(allowUndo)was.put(loc, multiblock.getBlock(loc));
            multiblock.setBlock(loc, block);
        }
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(BlockPos loc : was.keySet()){
            multiblock.setBlockExact(loc, was.get(loc));
        }
    }
    public SetblocksAction add(BlockPos pos){
        locations.add(pos);
        return this;
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<AbstractBlock> blocks){
        for(BlockPos loc : locations){
            AbstractBlock b = multiblock.getBlock(loc);
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
        HashSet<BlockPos> newLocs = new HashSet<>();
        BoundingBox bbox = multiblock.getBoundingBox();
        locations.forEach((t) -> symmetry.apply(t, bbox.getWidth(), bbox.getHeight(), bbox.getDepth(), newLocs::add));
        locations.addAll(newLocs);
    }
}