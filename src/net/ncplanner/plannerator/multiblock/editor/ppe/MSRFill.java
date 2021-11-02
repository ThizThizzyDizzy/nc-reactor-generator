package net.ncplanner.plannerator.multiblock.editor.ppe;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRFill extends PostProcessingEffect<OverhaulMSR>{
    private final Block block;
    public MSRFill(Block block){
        super("Fill with "+block.getDisplayName(), true, true, false);
        this.block = block;
    }
    @Override
    public void apply(OverhaulMSR multiblock, MultiblockGenerator generator){
        multiblock.forEachPosition((x, y, z) -> {
            if(multiblock.getBlock(x, y, z)==null||multiblock.getBlock(x,y,z).isConductor()||multiblock.getBlock(x,y,z).isInert())multiblock.setBlock(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(multiblock.getConfiguration(), x, y, z, block));
        });
    }
}