package multiblock.ppe;
import multiblock.Multiblock;
import multiblock.configuration.overhaul.fissionmsr.Block;
public class MSRFill extends PostProcessingEffect{
    private final Block block;
    public MSRFill(Block block){
        super("Fill "+block.name+"s", true, false);
        this.block = block;
    }
    @Override
    public void apply(Multiblock multiblock){
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    if(multiblock.getBlock(x, y, z)==null)multiblock.setBlock(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                }
            }
        }
    }
}