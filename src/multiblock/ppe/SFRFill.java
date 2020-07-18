package multiblock.ppe;
import planner.configuration.overhaul.fissionsfr.Block;
public class SFRFill extends PostProcessingEffect{
    public SFRFill(Block block){
        super("Fill "+block.name+"s");
    }
}