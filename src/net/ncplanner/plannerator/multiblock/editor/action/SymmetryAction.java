package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.editor.symmetry.Symmetry;
public class SymmetryAction extends Action<Multiblock>{
    private final Symmetry symmetry;
    private HashMap<BlockPos, AbstractBlock> was = new HashMap<>();
    public SymmetryAction(Symmetry symmetry){
        this.symmetry = symmetry;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        if(allowUndo){
            multiblock.forEachPosition((ps) -> {
                BlockPos pos = (BlockPos)ps;
                was.put(pos, multiblock.getBlock(pos));
            });
        }
        symmetry.apply(multiblock);
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(BlockPos pos : was.keySet()){
            multiblock.setBlockExact(pos, was.get(pos));
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<AbstractBlock> blocks){}
}