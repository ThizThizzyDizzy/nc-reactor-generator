package multiblock.ppe;
import multiblock.Multiblock;
import multiblock.configuration.overhaul.fissionsfr.Block;
public class SFRFill extends PostProcessingEffect{
    private final Block block;
    public SFRFill(Block block){
        super("Fill "+block.name+"s", true, false);
        this.block = block;
    }
    @Override
    public void apply(Multiblock multiblock){
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    if(multiblock.getBlock(x, y, z)==null)multiblock.setBlock(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                }
            }
        }
    }
}