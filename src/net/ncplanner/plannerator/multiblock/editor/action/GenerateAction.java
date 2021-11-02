package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
public class GenerateAction extends Action<Multiblock>{
    private final Multiblock multiblock;
    private HashMap<BlockPos, Block> was = new HashMap();
    public GenerateAction(Multiblock multiblock){
        this.multiblock = multiblock;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        multiblock.forEachPosition((x, y, z) -> {
            if(allowUndo)was.put(new BlockPos(x,y,z),multiblock.getBlock(x, y, z));
            Block block = this.multiblock.getBlock(x, y, z);
            multiblock.setBlock(x, y, z, block);
        });
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(BlockPos pos : was.keySet()){
            multiblock.setBlockExact(pos.x, pos.y, pos.z, was.get(pos));
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){
        //TODO only list the actually affected blocks
    }
}