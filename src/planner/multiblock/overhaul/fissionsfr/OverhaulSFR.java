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
            blocks.add(new Block(-1, -1, -1, block));
        }
    }
    @Override
    public int getMinSize(){
        return Core.configuration.overhaul.fissionSFR.minSize;
    }
    @Override
    public int getMaxSize(){
        return Core.configuration.overhaul.fissionSFR.maxSize;
    }
    @Override
    public void calculate(){
    }
    @Override
    protected Block newCasing(int x, int y, int z){
        return new Block(x, y, z, null);
    }
    @Override
    public String getTooltip(){
        return null;
    }
}