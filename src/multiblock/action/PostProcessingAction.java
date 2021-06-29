package multiblock.action;
import generator.Settings;
import java.util.ArrayList;
import java.util.HashMap;
import multiblock.Action;
import multiblock.Block;
import multiblock.BlockPos;
import multiblock.Multiblock;
import multiblock.ppe.PostProcessingEffect;
public class PostProcessingAction extends Action<Multiblock>{
    private final PostProcessingEffect postProcessingEffect;
    private HashMap<BlockPos, Block> was = new HashMap<>();
    private final Settings settings;
    public PostProcessingAction(PostProcessingEffect postProcessingEffect, Settings settings){
        this.postProcessingEffect = postProcessingEffect;
        this.settings = settings;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        if(allowUndo){
            multiblock.forEachPosition((x, y, z) -> {
                was.put(new BlockPos(x,y,z), multiblock.getBlock(x, y, z));
            });
        }
        postProcessingEffect.apply(multiblock, settings);
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