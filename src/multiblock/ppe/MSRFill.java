package multiblock.ppe;
import generator.Settings;
import multiblock.configuration.overhaul.fissionmsr.Block;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRFill extends PostProcessingEffect<OverhaulMSR>{
    private final Block block;
    public MSRFill(Block block){
        super("Fill with "+block.getDisplayName(), true, true, false);
        this.block = block;
    }
    @Override
    public void apply(OverhaulMSR multiblock, Settings settings){
        multiblock.forEachPosition((x, y, z) -> {
            if(multiblock.getBlock(x, y, z)==null||multiblock.getBlock(x,y,z).isConductor()||multiblock.getBlock(x,y,z).isInert())multiblock.setBlock(x, y, z, new multiblock.overhaul.fissionmsr.Block(multiblock.getConfiguration(), x, y, z, block));
        });
    }
}