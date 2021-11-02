package net.ncplanner.plannerator.multiblock.editor.ppe;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
public class FusionFill extends PostProcessingEffect<OverhaulFusionReactor>{
    private final Block block;
    public FusionFill(Block block){
        super("Fill with "+block.getDisplayName(), true, true, false);
        this.block = block;
    }
    @Override
    public void apply(OverhaulFusionReactor multiblock, MultiblockGenerator generator){
        multiblock.forEachPosition((x, y, z) -> {
            if(multiblock.getBlock(x, y, z)==null||multiblock.getBlock(x, y, z).isConductor()||multiblock.getBlock(x, y, z).isInert())multiblock.setBlock(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fusion.Block(multiblock.getConfiguration(), x, y, z, block));
        });
    }
}