package planner.multiblock.overhaul.fissionsfr;
import java.util.List;
import planner.Core;
import planner.multiblock.Multiblock;
public class OverhaulSFR extends Multiblock<Block>{
    public OverhaulSFR(){
        super(7, 5, 7);
    }
    @Override
    public String getDefinitionName(){
        return "Overhaul SFR";
    }
    @Override
    public OverhaulSFR newInstance(){
        return new OverhaulSFR();
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(Core.configuration==null||Core.configuration.overhaul==null||Core.configuration.overhaul.fissionSFR==null)return;
        for(planner.configuration.overhaul.fissionsfr.Block block : Core.configuration.overhaul.fissionSFR.blocks){
            blocks.add(new Block(block));
        }
    }
}