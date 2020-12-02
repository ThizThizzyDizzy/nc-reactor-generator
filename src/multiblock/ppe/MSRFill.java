package multiblock.ppe;
import generator.Settings;
import multiblock.configuration.overhaul.fissionmsr.Block;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRFill extends PostProcessingEffect<OverhaulMSR>{
    private final Block block;
    public MSRFill(Block block){
        super("Fill "+block.name+"s", true, true, false);
        this.block = block;
    }
    @Override
    public void apply(OverhaulMSR multiblock, Settings settings){
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    if(multiblock.getBlock(x, y, z)==null||multiblock.getBlock(x,y,z).isConductor()||multiblock.getBlock(x,y,z).isInert())multiblock.setBlock(x, y, z, new multiblock.overhaul.fissionmsr.Block(multiblock.getConfiguration(), x, y, z, block));
                }
            }
        }
    }
}