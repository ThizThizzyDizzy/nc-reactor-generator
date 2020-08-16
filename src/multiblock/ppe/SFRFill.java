package multiblock.ppe;
import generator.Settings;
import multiblock.configuration.overhaul.fissionsfr.Block;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
public class SFRFill extends PostProcessingEffect<OverhaulSFR>{
    private final Block block;
    public SFRFill(Block block){
        super("Fill "+block.name+"s", true, true, false);
        this.block = block;
    }
    @Override
    public void apply(OverhaulSFR multiblock, Settings settings){
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    if(multiblock.getBlock(x, y, z)==null||multiblock.getBlock(x, y, z).isConductor()||multiblock.getBlock(x, y, z).isInert())multiblock.setBlock(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                }
            }
        }
    }
}