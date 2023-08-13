package net.ncplanner.plannerator.multiblock.editor.ppe;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockElement;
public class FusionFill extends PostProcessingEffect<OverhaulFusionReactor>{
    private final BlockElement block;
    public FusionFill(BlockElement block){
        super("Fill with "+block.getDisplayName(), true, true, false);
        this.block = block;
    }
    @Override
    public void apply(OverhaulFusionReactor multiblock, MultiblockGenerator generator){
        multiblock.forEachPosition((x, y, z) -> {
            if(multiblock.getBlock(x, y, z)==null||multiblock.getBlock(x, y, z).isConductor())multiblock.setBlock(x, y, z, new Block(multiblock.getConfiguration(), x, y, z, block));
        });
    }
}