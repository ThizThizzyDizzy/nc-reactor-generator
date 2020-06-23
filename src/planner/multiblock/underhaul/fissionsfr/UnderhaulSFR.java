package planner.multiblock.underhaul.fissionsfr;
import java.util.List;
import planner.Core;
import planner.multiblock.Multiblock;
public class UnderhaulSFR extends Multiblock<Block>{
    public UnderhaulSFR(){
        super(7, 5, 7);
    }
    @Override
    public String getDefinitionName(){
        return "Underhaul SFR";
    }
    @Override
    public UnderhaulSFR newInstance(){
        return new UnderhaulSFR();
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(Core.configuration==null||Core.configuration.underhaul==null||Core.configuration.underhaul.fissionSFR==null)return;
        for(planner.configuration.underhaul.fissionsfr.Block block : Core.configuration.underhaul.fissionSFR.blocks){
            blocks.add(new Block(block));
        }
    }
}