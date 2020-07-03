package planner.multiblock.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.List;
import planner.Core;
import planner.configuration.Configuration;
import planner.configuration.underhaul.fissionsfr.Fuel;
import planner.multiblock.Multiblock;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigNumberList;
public class UnderhaulSFR extends Multiblock<Block>{
    private int netHeat, power, heat, cooling;
    private float efficiency;
    public Fuel fuel;
    private double heatMult;
    public UnderhaulSFR(){
        this(7, 5, 7, Core.configuration.underhaul.fissionSFR.fuels.get(0));
    }
    public UnderhaulSFR(int x, int y, int z, Fuel fuel){
        super(x, y, z);
        this.fuel = fuel;
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
    public void calculate(List<Block> blocks){
        for(Block block : blocks){
            block.calculateCore(this);
        }
        float totalHeatMult = 0;
        float totalEnergyMult = 0;
        int cells = 0;
        boolean somethingChanged;
        do{
            somethingChanged = false;
            for(Block block : blocks){
                if(block.calculateCooler(this))somethingChanged = true;
            }
        }while(somethingChanged);
        cooling = 0;
        for(Block block : getBlocks()){
            if(block.isFuelCell()){
                totalHeatMult+=block.heatMult;
                totalEnergyMult+=block.energyMult;
                cells++;
            }
            if(block.isCooler()&&block.isActive())cooling+=block.getCooling();
        }
        this.heatMult = totalHeatMult/cells;
        heat = (int) (totalHeatMult*fuel.heat);
        netHeat = heat-cooling;
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
                + "Total Heat: "+heat+"\n"
                + "Total Cooling: "+cooling+"\n"
                + "Net Heat: "+netHeat+"H/t\n"
                + "Efficiency: "+percent(efficiency, 0)+"\n"
                + "Heat multiplier: "+percent(heatMult, 0);
    }
    @Override
    public int getMultiblockID(){
        return 0;
    }
    @Override
    protected void save(Configuration configuration, Config config){
        ConfigNumberList size = new ConfigNumberList();
        size.add(getX());
        size.add(getY());
        size.add(getZ());
        config.set("size", size);
        config.set("fuel", (byte)configuration.underhaul.fissionSFR.fuels.indexOf(fuel));
        boolean compact = isCompact(configuration);//find perfect compression ratio
        config.set("compact", compact);
        ConfigNumberList blox = new ConfigNumberList();
        if(compact){
            for(int x = 0; x<getX(); x++){
                for(int y = 0; y<getY(); y++){
                    for(int z = 0; z<getZ(); z++){
                        Block block = getBlock(x, y, z);
                        if(block==null)blox.add(0);
                        else blox.add(configuration.underhaul.fissionSFR.blocks.indexOf(block.template)+1);
                    }
                }
            }
        }else{
            for(Block block : getBlocks()){
                blox.add(block.x);
                blox.add(block.y);
                blox.add(block.z);
                blox.add(configuration.underhaul.fissionSFR.blocks.indexOf(block.template)+1);
            }
        }
        config.set("blocks", blox);
    }
    private boolean isCompact(Configuration configuration){
        int blockCount = getBlocks().size();
        int volume = getX()*getY()*getZ();
        int bitsPerDim = logBase(2, Math.max(getX(), Math.max(getY(), getZ())));
        int bitsPerType = logBase(2, configuration.underhaul.fissionSFR.blocks.size());
        int compactBits = bitsPerType*volume;
        int spaciousBits = 4*Math.max(bitsPerDim, bitsPerType)*blockCount;
        return compactBits<spaciousBits;
    }
    private static int logBase(int base, int n){
        return (int)(Math.log(n)/Math.log(base));
    }
    @Override
    public void convertTo(Configuration to){
        if(to.underhaul==null||to.underhaul.fissionSFR==null)return;
        for(Block block : getBlocks()){
            block.template = to.underhaul.fissionSFR.convert(block.template);
        }
        fuel = to.underhaul.fissionSFR.convert(fuel);
    }
    @Override
    public boolean validate(){
        return false;
    }
    @Override
    public boolean exists(){
        return Core.configuration.underhaul!=null&&Core.configuration.underhaul.fissionSFR!=null;
    }
}