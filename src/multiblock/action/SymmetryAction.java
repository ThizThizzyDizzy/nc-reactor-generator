package multiblock.action;
import java.util.ArrayList;
import java.util.HashMap;
import multiblock.Action;
import multiblock.Block;
import multiblock.BlockPos;
import multiblock.Multiblock;
import multiblock.symmetry.Symmetry;
public class SymmetryAction extends Action<Multiblock>{
    private final Symmetry symmetry;
    private HashMap<BlockPos, Block> was = new HashMap<>();
    public SymmetryAction(Symmetry symmetry){
        this.symmetry = symmetry;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        if(allowUndo){
            multiblock.forEachPosition((x, y, z) -> {
                was.put(new BlockPos(x,y,z), multiblock.getBlock(x, y, z));
            });
        }
        symmetry.apply(multiblock);
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(BlockPos pos : was.keySet()){
            multiblock.setBlockExact(pos.x, pos.y, pos.z, was.get(pos));
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){}
}