package multiblock.underhaul.fissionsfr;
import generator.Priority;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import multiblock.configuration.Configuration;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import multiblock.Multiblock;
import multiblock.PartCount;
import multiblock.ppe.ClearInvalid;
import multiblock.ppe.PostProcessingEffect;
import multiblock.ppe.SmartFillUnderhaulSFR;
import multiblock.symmetry.AxialSymmetry;
import multiblock.symmetry.Symmetry;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentMinimaList;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigNumberList;
public class UnderhaulSFR extends Multiblock<Block>{
    public int netHeat;
    private int power, heat, cooling, cells;
    private float efficiency;
    public Fuel fuel;
    private double heatMult;
    public UnderhaulSFR(){
        this(7, 5, 7, null);
    }
    public UnderhaulSFR(int x, int y, int z, Fuel fuel){
        super(x, y, z);
        this.fuel = fuel==null?getConfiguration().underhaul.fissionSFR.allFuels.get(0):fuel;
    }
    @Override
    public String getDefinitionName(){
        return "Underhaul SFR";
    }
    @Override
    public UnderhaulSFR newInstance(Configuration configuration){
        UnderhaulSFR sfr = new UnderhaulSFR();
        sfr.setConfiguration(configuration);
        return sfr;
    }
    @Override
    public Multiblock<Block> newInstance(int x, int y, int z){
        return new UnderhaulSFR(x, y, z, null);
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(getConfiguration()==null||getConfiguration().underhaul==null||getConfiguration().underhaul.fissionSFR==null)return;
        for(multiblock.configuration.underhaul.fissionsfr.Block block : getConfiguration().underhaul.fissionSFR.allBlocks){
            blocks.add(new Block(-1,-1,-1,block));
        }
    }
    @Override
    public int getMinX(){
        return getConfiguration().underhaul.fissionSFR.minSize;
    }
    @Override
    public int getMinY(){
        return getConfiguration().underhaul.fissionSFR.minSize;
    }
    @Override
    public int getMinZ(){
        return getConfiguration().underhaul.fissionSFR.minSize;
    }
    @Override
    public int getMaxX(){
        return getConfiguration().underhaul.fissionSFR.maxSize;
    }
    @Override
    public int getMaxY(){
        return getConfiguration().underhaul.fissionSFR.maxSize;
    }
    @Override
    public int getMaxZ(){
        return getConfiguration().underhaul.fissionSFR.maxSize;
    }
    @Override
    public void calculate(List<Block> blocks){
        for(Block block : blocks){
            block.calculateCore(this);
        }
        float totalHeatMult = 0;
        float totalEnergyMult = 0;
        cells = 0;
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
        if(Double.isNaN(heatMult))heatMult = 0;
        heat = (int) (totalHeatMult*fuel.heat);
        netHeat = heat-cooling;
        power = (int) (totalEnergyMult*fuel.power);
        efficiency = totalEnergyMult/cells;
        if(Double.isNaN(efficiency))efficiency = 0;
    }
    @Override
    protected Block newCasing(int x, int y, int z){
        return new Block(x, y, z, null);
    }
    @Override
    protected String getExtraSaveTooltip(){
        return "Fuel: "+fuel.name;
    }
    @Override
    protected String getExtraBotTooltip(){
        return getTooltip();
    }
    @Override
    public String getTooltip(){
        return "Power Generation: "+power+"RF/t\n"
                + "Total Heat: "+heat+"H/t\n"
                + "Total Cooling: "+cooling+"H/t\n"
                + "Net Heat: "+netHeat+"H/t\n"
                + "Efficiency: "+percent(efficiency, 0)+"\n"
                + "Heat multiplier: "+percent(heatMult, 0)+"\n"
                + "Fuel cells: "+cells;
    }
    @Override
    public int getMultiblockID(){
        return 0;
    }
    @Override
    protected void save(NCPFFile ncpf, Configuration configuration, Config config){
        ConfigNumberList size = new ConfigNumberList();
        size.add(getX());
        size.add(getY());
        size.add(getZ());
        config.set("size", size);
        config.set("fuel", (byte)configuration.underhaul.fissionSFR.allFuels.indexOf(fuel));
        boolean compact = isCompact(configuration);//find perfect compression ratio
        config.set("compact", compact);
        ConfigNumberList blox = new ConfigNumberList();
        if(compact){
            for(int x = 0; x<getX(); x++){
                for(int y = 0; y<getY(); y++){
                    for(int z = 0; z<getZ(); z++){
                        Block block = getBlock(x, y, z);
                        if(block==null)blox.add(0);
                        else blox.add(configuration.underhaul.fissionSFR.allBlocks.indexOf(block.template)+1);
                    }
                }
            }
        }else{
            for(Block block : getBlocks()){
                blox.add(block.x);
                blox.add(block.y);
                blox.add(block.z);
                blox.add(configuration.underhaul.fissionSFR.allBlocks.indexOf(block.template)+1);
            }
        }
        config.set("blocks", blox);
    }
    private boolean isCompact(Configuration configuration){
        int blockCount = getBlocks().size();
        int volume = getX()*getY()*getZ();
        int bitsPerDim = logBase(2, Math.max(getX(), Math.max(getY(), getZ())));
        int bitsPerType = logBase(2, configuration.underhaul.fissionSFR.allBlocks.size());
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
        setConfiguration(to);
    }
    @Override
    public boolean validate(){
        return false;
    }
    @Override
    public boolean exists(){
        return getConfiguration().underhaul!=null&&getConfiguration().underhaul.fissionSFR!=null;
    }
    @Override
    public void addGeneratorSettings(MenuComponentMinimaList multiblockSettings){}
    private boolean isValid(){
        return power>0;
    }
    @Override
    public void getGenerationPriorities(ArrayList<Priority> priorities){
        priorities.add(new Priority<UnderhaulSFR>("Valid (>0 output)", true){
            @Override
            protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                if(main.isValid()&&!other.isValid())return 1;
                if(!main.isValid()&&other.isValid())return -1;
                return 0;
            }
        });
        priorities.add(new Priority<UnderhaulSFR>("Stability", false){
            @Override
            protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                return Math.max(0, other.netHeat)-Math.max(0, main.netHeat);
            }
        });
        priorities.add(new Priority<UnderhaulSFR>("Efficiency", true){
            @Override
            protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                return main.efficiency-other.efficiency;
            }
        });
        priorities.add(new Priority<UnderhaulSFR>("Output", true){
            @Override
            protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                return main.power-other.power;
            }
        });
        priorities.add(new Priority<UnderhaulSFR>("Minimize Heat", false){
            @Override
            protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                return other.heat-main.heat;
            }
        });
        priorities.add(new Priority<UnderhaulSFR>("Fuel usage", true){
            @Override
            protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                return main.cells-other.cells;
            }
        });
    }
    @Override
    public void getGenerationPriorityPresets(ArrayList<Priority> priorities, ArrayList<Priority.Preset> presets){
        presets.add(new Priority.Preset("Efficiency", priorities.get(0), priorities.get(1), priorities.get(2), priorities.get(3), priorities.get(4)).addAlternative("Efficient"));
        presets.add(new Priority.Preset("Output", priorities.get(0), priorities.get(1), priorities.get(3), priorities.get(2), priorities.get(4)));
        presets.add(new Priority.Preset("Fuel Usage (Breeder)", priorities.get(0), priorities.get(1), priorities.get(5), priorities.get(4), priorities.get(3), priorities.get(2)).addAlternative("Fuel Usage").addAlternative("Speed").addAlternative("Cell Count").addAlternative("Breeder").addAlternative("Fast"));
    }
    @Override
    public void getSymmetries(ArrayList<Symmetry> symmetries){
        symmetries.add(AxialSymmetry.X);
        symmetries.add(AxialSymmetry.Y);
        symmetries.add(AxialSymmetry.Z);
    }
    @Override
    public void getPostProcessingEffects(ArrayList<PostProcessingEffect> postProcessingEffects){
        postProcessingEffects.add(new ClearInvalid());
        postProcessingEffects.add(new SmartFillUnderhaulSFR());
    }
    @Override
    public UnderhaulSFR blankCopy(){
        return new UnderhaulSFR(getX(), getY(), getZ(), fuel);
    }
    @Override
    public UnderhaulSFR copy(){
        UnderhaulSFR copy = blankCopy();
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block get = getBlock(x, y, z);
                    if(get!=null)copy.setBlockExact(x, y, z, get.copy());
                }
            }
        }
        copy.netHeat = netHeat;
        copy.power = power;
        copy.heat = heat;
        copy.cooling = cooling;
        copy.cells = cells;
        copy.efficiency = efficiency;
        copy.heatMult = heatMult;
        return copy;
    }
    @Override
    protected int doCount(Object o){
        throw new IllegalArgumentException("Cannot count "+o.getClass().getName()+" in "+getDefinitionName()+"!");
    }
    @Override
    public String getGeneralName(){
        return "Reactor";
    }
    @Override
    public boolean isCompatible(Multiblock<Block> other){
        return ((UnderhaulSFR)other).fuel==fuel;
    }
    @Override
    protected void getFluidOutputs(HashMap<String, Double> outputs){}
    @Override
    protected void getExtraParts(ArrayList<PartCount> parts){
        parts.add(new PartCount(null, "Casing", getX()*getZ()*2+getX()*getY()*2+getY()*getZ()*2));
    }
    @Override
    public String getDescriptionTooltip(){
        return "Underhaul SFRs are Solid-Fueled Fission reactors in NuclearCraft\nIf you have blocks called \"Heat Sink\" instead of \"Cooler\", you are playing Overhaul";
    }
}