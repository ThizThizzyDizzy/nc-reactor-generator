package multiblock.overhaul.fissionmsr;
import generator.Priority;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import planner.Core;
import planner.configuration.Configuration;
import planner.configuration.overhaul.fissionmsr.Fuel;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.action.MSRAllShieldsAction;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import planner.menu.component.MenuComponentMSRToggleFuel;
import planner.menu.component.MenuComponentMinimaList;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigNumberList;
public class OverhaulMSR extends Multiblock<Block>{
    public ArrayList<Cluster> clusters = new ArrayList<>();
    public int totalFuelCells;
    public int totalCooling;
    public int totalHeat;
    public int netHeat;
    public float totalEfficiency;
    public float totalHeatMult;
    public int totalIrradiation;
    public int functionalBlocks;
    public float sparsityMult;
    public HashMap<String, Float> totalOutput = new HashMap<>();
    public float totalTotalOutput;
    public OverhaulMSR(){
        this(7, 5, 7);
    }
    public OverhaulMSR(int x, int y, int z){
        super(x, y, z);
    }
    @Override
    public String getDefinitionName(){
        return "Overhaul MSR";
    }
    @Override
    public OverhaulMSR newInstance(){
        return new OverhaulMSR();
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(Core.configuration==null||Core.configuration.overhaul==null||Core.configuration.overhaul.fissionMSR==null)return;
        for(planner.configuration.overhaul.fissionmsr.Block block : Core.configuration.overhaul.fissionMSR.blocks){
            blocks.add(new Block(-1, -1, -1, block));
        }
    }
    @Override
    public int getMinSize(){
        return Core.configuration.overhaul.fissionMSR.minSize;
    }
    @Override
    public int getMaxSize(){
        return Core.configuration.overhaul.fissionMSR.maxSize;
    }
    @Override
    public void calculate(List<Block> blocks){
        List<Block> allBlocks = getBlocks();
        for(Block block : blocks){
            if(block.isPrimed())block.propogateNeutronFlux(this);
        }
        for(Block block : blocks){
            if(block.isFuelVessel())block.postFluxCalc(this);
        }
        boolean somethingChanged;
        do{
            somethingChanged = false;
            for(Block block : blocks){
                if(block.calculateHeater(this))somethingChanged = true;
            }
        }while(somethingChanged);
        for(Block block : blocks){//set cell efficiencies
            if(block.isFuelVessel()){
                float criticalityModifier = (float) (1/(1+Math.exp(2*(block.neutronFlux-2*block.fuel.criticality))));
                block.efficiency = block.fuel.efficiency*block.positionalEfficiency*(block.source==null?1:block.source.efficiency)*criticalityModifier;
            }
        }
        for(Block block : allBlocks){//detect clusters
            Cluster cluster = getCluster(block);
            if(cluster==null)continue;//that's not a cluster!
            if(clusters.contains(cluster))continue;//already know about that one!
            clusters.add(cluster);
        }
        for(Cluster cluster : clusters){
            int fuelCells = 0;
            for(Block b : cluster.blocks){
                if(b.isFuelVesselActive()){
                    fuelCells++;
                    cluster.efficiency+=b.efficiency;
                    cluster.totalHeat+=b.moderatorLines*b.fuel.heat;
                    cluster.heatMult+=b.moderatorLines;
                }
                if(b.isHeaterActive()){
                    cluster.totalCooling+=b.template.cooling;
                }
                if(b.isShieldActive()){
                    cluster.totalHeat+=b.template.heatMult*b.neutronFlux;
                }
                if(b.isIrradiatorActive()){
                    cluster.irradiation+=b.neutronFlux;
                    if(b.recipe!=null)cluster.totalHeat+=b.recipe.heat*b.neutronFlux;
                }
            }
            cluster.efficiency/=fuelCells;
            cluster.heatMult/=fuelCells;
            if(Double.isNaN(cluster.efficiency))cluster.efficiency = 0;
            if(Double.isNaN(cluster.heatMult))cluster.heatMult = 0;
            cluster.netHeat = cluster.totalHeat-cluster.totalCooling;
            if(cluster.totalCooling==0)cluster.coolingPenaltyMult = 1;
            else cluster.coolingPenaltyMult = Math.min(1, (cluster.totalHeat+Core.configuration.overhaul.fissionMSR.coolingEfficiencyLeniency)/(float)cluster.totalCooling);
            cluster.efficiency*=cluster.coolingPenaltyMult;
            totalFuelCells+=fuelCells;
            totalCooling+=cluster.totalCooling;
            totalHeat+=cluster.totalHeat;
            netHeat+=cluster.netHeat;
            totalEfficiency+=cluster.efficiency*fuelCells;
            totalHeatMult+=cluster.heatMult*fuelCells;
            totalIrradiation+=cluster.irradiation;
        }
        totalEfficiency/=totalFuelCells;
        totalHeatMult/=totalFuelCells;
        if(Double.isNaN(totalEfficiency))totalEfficiency = 0;
        if(Double.isNaN(totalHeatMult))totalHeatMult = 0;
        functionalBlocks = 0;
        for(Block block : allBlocks){
            if(block.isFunctional())functionalBlocks++;
        }
        int volume = getX()*getY()*getZ();
        sparsityMult = (float) (functionalBlocks/(float)volume>=Core.configuration.overhaul.fissionMSR.sparsityPenaltyThreshold?1:Core.configuration.overhaul.fissionMSR.sparsityPenaltyMult+(1-Core.configuration.overhaul.fissionMSR.sparsityPenaltyMult)*Math.sin(Math.PI*functionalBlocks/(2*volume*Core.configuration.overhaul.fissionMSR.sparsityPenaltyThreshold)));
        totalEfficiency*=sparsityMult;
        for(Cluster c : clusters){
            for(Block b : c.blocks){
                if(b.template.cooling>0){
                    float out = c.efficiency*sparsityMult;
                    totalOutput.put(b.template.output, (totalOutput.containsKey(b.template.output)?totalOutput.get(b.template.output):0)+out);
                    totalTotalOutput+=out;
                }
            }
        }
    }
    @Override
    protected Block newCasing(int x, int y, int z){
        return new Block(x, y, z, null);
    }
    @Override
    public String getTooltip(){
        String outs = "";
        ArrayList<String> outputList = new ArrayList<>(totalOutput.keySet());
        Collections.sort(outputList);
        for(String s : outputList){
            outs+="\n "+Math.round(totalOutput.get(s))+" mb/t of "+s;
        }
        String s = "Total output: "+Math.round(totalTotalOutput)+" mb/t"+outs+"\n"
                + "Total Heat: "+totalHeat+"H/t\n"
                + "Total Cooling: "+totalCooling+"H/t\n"
                + "Net Heat: "+netHeat+"H/t\n"
                + "Overall Efficiency: "+percent(totalEfficiency, 0)+"\n"
                + "Overall Heat Multiplier: "+percent(totalHeatMult, 0)+"\n"
                + "Sparsity Penalty Multiplier: "+Math.round(sparsityMult*10000)/10000d+"\n"
                + "Clusters: "+clusters.size()+"\n"
                + "Total Irradiation: "+totalIrradiation+"\n";
        for(Fuel f : Core.configuration.overhaul.fissionMSR.fuels){
            int i = getFuelCount(f);
            if(i>0)s+="\n"+f.name+": "+i;
        }
        for(Cluster c : clusters){
            s+="\n\n"+c.getTooltip();
        }
        return s;
    }
    @Override
    public int getMultiblockID(){
        return 2;
    }
    @Override
    protected void save(Configuration configuration, Config config){
        ConfigNumberList size = new ConfigNumberList();
        size.add(getX());
        size.add(getY());
        size.add(getZ());
        config.set("size", size);
        boolean compact = isCompact(configuration);//find perfect compression ratio
        config.set("compact", compact);
        ConfigNumberList blox = new ConfigNumberList();
        if(compact){
            for(int x = 0; x<getX(); x++){
                for(int y = 0; y<getY(); y++){
                    for(int z = 0; z<getZ(); z++){
                        Block block = getBlock(x, y, z);
                        if(block==null)blox.add(0);
                        else blox.add(configuration.overhaul.fissionMSR.blocks.indexOf(block.template)+1);
                    }
                }
            }
        }else{
            for(Block block : getBlocks()){
                blox.add(block.x);
                blox.add(block.y);
                blox.add(block.z);
                blox.add(configuration.overhaul.fissionMSR.blocks.indexOf(block.template)+1);
            }
        }
        ConfigNumberList fuels = new ConfigNumberList();
        ConfigNumberList sources = new ConfigNumberList();
        ConfigNumberList irradiatorRecipes = new ConfigNumberList();
        for(Block block : getBlocks()){
            if(block.template.fuelVessel)fuels.add(configuration.overhaul.fissionMSR.fuels.indexOf(block.fuel));
            if(block.template.fuelVessel)sources.add(configuration.overhaul.fissionMSR.sources.indexOf(block.source)+1);
            if(block.template.irradiator)irradiatorRecipes.add(configuration.overhaul.fissionMSR.irradiatorRecipes.indexOf(block.recipe)+1);
        }
        config.set("blocks", blox);
        config.set("fuels", fuels);
        config.set("sources", sources);
        config.set("irradiatorRecipes", irradiatorRecipes);
    }
    private boolean isCompact(Configuration configuration){
        int blockCount = getBlocks().size();
        int volume = getX()*getY()*getZ();
        int bitsPerDim = logBase(2, Math.max(getX(), Math.max(getY(), getZ())));
        int bitsPerType = logBase(2, configuration.overhaul.fissionMSR.blocks.size());
        int compactBits = bitsPerType*volume;
        int spaciousBits = 4*Math.max(bitsPerDim, bitsPerType)*blockCount;
        return compactBits<spaciousBits;
    }
    private static int logBase(int base, int n){
        return (int)(Math.log(n)/Math.log(base));
    }
    @Override
    public void convertTo(Configuration to){
        if(to.overhaul==null||to.overhaul.fissionMSR==null)return;
        for(Block block : getBlocks()){
            if(block.template.fuelVessel)block.fuel = to.overhaul.fissionMSR.convert(block.fuel);
            if(block.template.fuelVessel)block.source = to.overhaul.fissionMSR.convert(block.source);
            if(block.template.irradiator)block.recipe = to.overhaul.fissionMSR.convert(block.recipe);
            block.template = to.overhaul.fissionMSR.convert(block.template);
        }
    }
    @Override
    public boolean validate(){
        boolean changed = false;
        BLOCKS:for(Block block : getBlocks()){
            if(block.source!=null){
                for(Direction d : directions){
                    int i = 0;
                    while(true){
                        i++;
                        Block b = getBlock(block.x+d.x*i, block.y+d.y*i, block.z+d.z*i);
                        if(b==null)continue;//air
                        if(b.isCasing())continue BLOCKS;
                        if(b.template.blocksLOS){
                            break;
                        }
                    }
                }
                block.source = null;
                changed = true;
            }
        }
        return changed;
    }
    public Cluster getCluster(Block block){
        if(block==null)return null;
        if(!block.canCluster())return null;
        for(Cluster cluster : clusters){
            if(cluster.contains(block))return cluster;
        }
        return new Cluster(block);
    }
    private int getFuelCount(Fuel f){
        int count = 0;
        for(Block block : getBlocks()){
            if(block.fuel==f)count++;
        }
        return count;
    }
    public OverhaulSFR convertToSFR(){
        OverhaulSFR sfr = new OverhaulSFR(getX(), getY(), getZ(), Core.configuration.overhaul.fissionSFR.coolantRecipes.get(0));
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block b = getBlock(x, y, z);
                    sfr.blocks[x][y][z] = b==null?null:b.convertToSFR();
                }
            }
        }
        sfr.metadata.putAll(metadata);
        return sfr;
    }
    @Override
    public void addGeneratorSettings(MenuComponentMinimaList multiblockSettings){
        for(Fuel f : Core.configuration.overhaul.fissionMSR.fuels){
            multiblockSettings.add(new MenuComponentMSRToggleFuel(f));
        }
    }
    private boolean isValid(){
        return totalTotalOutput>0;
    }
    private int getBadVessels(){
        int badVessels = 0;
        for(Block b : getBlocks()){
            if(b.isFuelVessel()&&!b.isFuelVesselActive())badVessels++;
        }
        return badVessels;
    }
    private float getShutdownFactor(){
        action(new MSRAllShieldsAction(true));
        float offOut = totalTotalOutput;
        undo();
        return 1-(offOut/totalTotalOutput);
    }
    @Override
    public void getGenerationPriorities(ArrayList<Priority> priorities){
        priorities.add(new Priority<OverhaulMSR>("Valid (>0 output)"){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                if(main.isValid()&&!other.isValid())return 1;
                if(!main.isValid()&&other.isValid())return -1;
                return 0;
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Minimize Bad Vessels"){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return other.getBadVessels()-main.getBadVessels();
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Shutdownable"){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return main.getShutdownFactor()-other.getShutdownFactor();
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Stability"){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return Math.max(0, other.netHeat)-Math.max(0, main.netHeat);
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Efficiency"){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return (int) Math.round(main.totalEfficiency*100-other.totalEfficiency*100);
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Output"){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return main.totalTotalOutput-other.totalTotalOutput;
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Irradiation"){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return main.totalIrradiation-other.totalIrradiation;
            }
        });
    }
    public class Cluster{
        public ArrayList<Block> blocks = new ArrayList<>();
        public boolean isConnectedToWall = false;
        public float efficiency;
        public int totalHeat, totalCooling, netHeat;
        public float heatMult, coolingPenaltyMult;
        public int irradiation;
        public Cluster(Block block){
            blocks.addAll(toList(getClusterBlocks(block, false)));
            isConnectedToWall = wallCheck(blocks);
            if(!isConnectedToWall){
                isConnectedToWall = wallCheck(toList(getClusterBlocks(block, true)));
            }
            if(isValid()){
                for(Block b : blocks){
                    b.inCluster = true;
                }
            }
        }
        private boolean isValid(){
            if(!isConnectedToWall)return false;
            for(Block block : blocks){
                if(block.template.createCluster)return true;
            }
            return false;
        }
        public boolean contains(Block block){
            return blocks.contains(block);
        }
        private boolean wallCheck(ArrayList<Block> blocks){
            for(Block block : blocks){
                if(block.x==0||block.y==0||block.z==0)return true;
                if(block.x==getX()-1||block.y==getY()-1||block.z==getZ()-1)return true;
            }
            return false;
        }
        public String getTooltip(){
            return "Efficiency: "+percent(efficiency, 0)+"\n"
                + "Total Heating: "+totalHeat+"H/t\n"
                + "Total Cooling: "+totalCooling+"H/t\n"
                + "Net Heating: "+netHeat+"H/t\n"
                + "Heat Multiplier: "+percent(heatMult, 0)+"\n"
                + "Cooling penalty mult: "+Math.round(coolingPenaltyMult*10000)/10000d;
        }
    }
    @Override
    public void clearData(List<Block> blocks){
        super.clearData(blocks);
        clusters.clear();
        totalOutput.clear();
        totalTotalOutput = totalEfficiency = totalHeatMult = sparsityMult = totalFuelCells = totalCooling = totalHeat = netHeat = totalIrradiation = functionalBlocks = 0;
    }
    /**
     * Block search algorithm from my Tree Feller for Bukkit.
     */
    private HashMap<Integer, ArrayList<Block>> getClusterBlocks(Block start, boolean useConductors){
        //layer zero
        HashMap<Integer, ArrayList<Block>>results = new HashMap<>();
        ArrayList<Block> zero = new ArrayList<>();
        if(start.canCluster()||(useConductors&&start.isConductor())){
            zero.add(start);
        }
        results.put(0, zero);
        //all the other layers
        int maxDistance = getX()*getY()*getZ();//the algorithm requires a max search distance. Rather than changing that, I'll just be lazy and give it a big enough number
        for(int i = 0; i<maxDistance; i++){
            ArrayList<Block> layer = new ArrayList<>();
            ArrayList<Block> lastLayer = new ArrayList<>(results.get(i));
            if(i==0&&lastLayer.isEmpty()){
                lastLayer.add(start);
            }
            for(Block block : lastLayer){
                FOR:for(int j = 0; j<6; j++){
                    int dx=0,dy=0,dz=0;
                    switch(j){//This is a primitive version of the Direction class used in other places here, but I'll just leave it as it is
                        case 0:
                            dx = -1;
                            break;
                        case 1:
                            dx = 1;
                            break;
                        case 2:
                            dy = -1;
                            break;
                        case 3:
                            dy = 1;
                            break;
                        case 4:
                            dz = -1;
                            break;
                        case 5:
                            dz = 1;
                            break;
                        default:
                            throw new IllegalArgumentException("How did this happen?");
                    }
                    Block newBlock = getBlock(block.x+dx,block.y+dy,block.z+dz);
                    if(newBlock==null)continue;
                    if(!(newBlock.canCluster()||(useConductors&&newBlock.isConductor()))){//that's not part of this bunch
                        continue;
                    }
                    for(Block oldbl : lastLayer){//if(lastLayer.contains(newBlock))continue;//if the new block is on the same layer, ignore
                        if(oldbl==newBlock){
                            continue FOR;
                        }
                    }
                    if(i>0){
                        for(Block oldbl : results.get(i-1)){//if(i>0&&results.get(i-1).contains(newBlock))continue;//if the new block is on the previous layer, ignore
                            if(oldbl==newBlock){
                                continue FOR;
                            }
                        }
                    }
                    for(Block oldbl : layer){//if(layer.contains(newBlock))continue;//if the new block is on the next layer, but already processed, ignore
                        if(oldbl==newBlock){
                            continue FOR;
                        }
                    }
                    layer.add(newBlock);
                }
            }
            if(layer.isEmpty())break;
            results.put(i+1, layer);
        }
        return results;
    }
    /**
     * Converts the tiered search returned by getBlocks into a list of blocks.<br>
     * Also from my tree feller
     */
    private static ArrayList<Block> toList(HashMap<Integer, ArrayList<Block>> blocks){
        ArrayList<Block> list = new ArrayList<>();
        for(int i : blocks.keySet()){
            list.addAll(blocks.get(i));
        }
        return list;
    }
    @Override
    public boolean exists(){
        return Core.configuration.overhaul!=null&&Core.configuration.overhaul.fissionMSR!=null;
    }
}