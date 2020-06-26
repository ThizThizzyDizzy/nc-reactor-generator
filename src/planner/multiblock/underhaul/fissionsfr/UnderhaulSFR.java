package planner.multiblock.underhaul.fissionsfr;
import java.util.List;
import planner.Core;
import planner.configuration.underhaul.fissionsfr.Fuel;
import planner.multiblock.Multiblock;
import simplelibrary.Queue;
public class UnderhaulSFR extends Multiblock<Block>{
    private int heat, power;
    private float efficiency;
    public Fuel fuel;
    public UnderhaulSFR(){
        super(7, 5, 7);
        fuel = Core.configuration.underhaul.fissionSFR.fuels.get(0);
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
            blocks.add(new Block(-1,-1,-1,block));
        }
    }
    @Override
    public int getMinSize(){
        return Core.configuration.underhaul.fissionSFR.minSize;
    }
    @Override
    public int getMaxSize(){
        return Core.configuration.underhaul.fissionSFR.maxSize;
    }
    @Override
    public void calculate(){
        Queue<Block> blocks = getBlocks();
        for(Block block : blocks){
            block.calculateCore(this);
        }
        float totalHeatMult = 0;
        float totalEnergyMult = 0;
        int cells = 0;
        for(Block block : blocks){
            block.calculateCooler(this);
        }
        int cooling = 0;
        for(Block block : blocks){
            if(block.isFuelCell()){
                totalHeatMult+=block.heatMult;
                totalEnergyMult+=block.energyMult;
                cells++;
            }
            if(block.isCooler()&&block.isActive())cooling+=block.getCooling();
        }
        heat = (int) (totalHeatMult*fuel.heat)-cooling;
        power = (int) (totalEnergyMult*fuel.power);
        efficiency = totalEnergyMult/cells;
    }
    @Override
    protected Block newCasing(int x, int y, int z){
        return new Block(x, y, z, null);
    }
    @Override
    public String getTooltip(){
        return "Power Generation: "+power+"RF/t\n"
                + "Heat: "+heat+"H/t\n"
                + "Efficiency: "+percent(efficiency, 0);
    }
}