package multiblock.ppe;
import planner.configuration.overhaul.fissionmsr.Block;
public class MSRFill extends PostProcessingEffect{
    public MSRFill(Block block){
        super("Fill "+block.name+"s");
    }
}