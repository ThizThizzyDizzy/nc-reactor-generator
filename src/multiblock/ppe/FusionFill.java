package multiblock.ppe;
import generator.Settings;
import multiblock.configuration.overhaul.fusion.Block;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
public class FusionFill extends PostProcessingEffect<OverhaulFusionReactor>{
    private final Block block;
    public FusionFill(Block block){
        super("Fill "+block.name+"s", true, true, false);
        this.block = block;
    }
    @Override
    public void apply(OverhaulFusionReactor multiblock, Settings settings){
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    if(multiblock.getBlock(x, y, z)==null||multiblock.getBlock(x, y, z).isConductor()||multiblock.getBlock(x, y, z).isInert())multiblock.setBlock(x, y, z, new multiblock.overhaul.fusion.Block(x, y, z, block));
                }
            }
        }
    }
}