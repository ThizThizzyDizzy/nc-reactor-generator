package multiblock.overhaul.fissionsfr;
import generator.Priority;
import generator.challenger.Challenger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import multiblock.Action;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import multiblock.configuration.overhaul.fissionsfr.Fuel;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.PartCount;
import multiblock.Range;
import multiblock.ppe.PostProcessingEffect;
import multiblock.action.SFRAllShieldsAction;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.ppe.ClearInvalid;
import multiblock.ppe.SFRFill;
import multiblock.symmetry.AxialSymmetry;
import multiblock.symmetry.Symmetry;
import multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe;
import multiblock.configuration.overhaul.fissionsfr.Source;
import multiblock.ppe.SmartFillOverhaulSFR;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentSFRToggleFuel;
import planner.menu.component.MenuComponentSFRToggleSource;
import planner.menu.component.MenuComponentSFRToggleIrradiatorRecipe;
import simplelibrary.Stack;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigNumberList;
public class OverhaulSFR extends Multiblock<Block>{
    public CoolantRecipe coolantRecipe;
    public ArrayList<Cluster> clusters = new ArrayList<>();
    public int totalFuelCells;
    public int rawOutput;
    public float totalOutput;
    public int totalCooling;
    public int totalHeat;
    public int netHeat;
    public float totalEfficiency;
    public float totalHeatMult;
    public int totalIrradiation;
    public int functionalBlocks;
    public float sparsityMult;
    public float shutdownFactor;
    public float rainbowScore;
    private boolean computingShutdown = false;
    public OverhaulSFR(){
        this(7, 5, 7, null);
    }
    public OverhaulSFR(int x, int y, int z, CoolantRecipe coolantRecipe){
        super(x, y, z);
        this.coolantRecipe = coolantRecipe==null?getConfiguration().overhaul.fissionSFR.allCoolantRecipes.get(0):coolantRecipe;
    }
    @Override
    public String getDefinitionName(){
        return "Overhaul SFR";
    }
    @Override
    public OverhaulSFR newInstance(Configuration configuration){
        OverhaulSFR sfr = new OverhaulSFR();
        sfr.setConfiguration(configuration);
        return sfr;
    }
    @Override
    public Multiblock<Block> newInstance(int x, int y, int z){
        return new OverhaulSFR(x, y, z, null);
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(getConfiguration()==null||getConfiguration().overhaul==null||getConfiguration().overhaul.fissionSFR==null)return;
        for(multiblock.configuration.overhaul.fissionsfr.Block block : getConfiguration().overhaul.fissionSFR.allBlocks){
            blocks.add(new Block(-1, -1, -1, block));
        }
    }
    @Override
    public int getMinX(){
        return getConfiguration().overhaul.fissionSFR.minSize;
    }
    @Override
    public int getMinY(){
        return getConfiguration().overhaul.fissionSFR.minSize;
    }
    @Override
    public int getMinZ(){
        return getConfiguration().overhaul.fissionSFR.minSize;
    }
    @Override
    public int getMaxX(){
        return getConfiguration().overhaul.fissionSFR.maxSize;
    }
    @Override
    public int getMaxY(){
        return getConfiguration().overhaul.fissionSFR.maxSize;
    }
    @Override
    public int getMaxZ(){
        return getConfiguration().overhaul.fissionSFR.maxSize;
    }
    @Override
    public void calculate(List<Block> blocks){
        List<Block> allBlocks = getBlocks();
        for(Block block : blocks){
            if(block.isPrimed())block.propogateNeutronFlux(this);
        }
        int lastActive, nowActive;
        do{
            lastActive = 0;
            for(Block block : blocks){
                boolean wasActive = block.isFuelCellActive();
                block.hadFlux = block.neutronFlux;
                block.clearData();
                if(wasActive)lastActive++;
                block.wasActive = wasActive;
            }
            for(Block block : blocks){
                block.rePropogateNeutronFlux(this);
            }
            nowActive = 0;
            for(Block block : blocks){
                if(block.isFuelCellActive())nowActive++;
                if(block.isFuelCell()&&!block.wasActive){
                    block.neutronFlux = block.hadFlux;
                }
            }
        }while(nowActive!=lastActive);
        for(Block block : blocks){
            if(block.isFuelCell())block.postFluxCalc(this);
        }
        boolean somethingChanged;
        do{
            somethingChanged = false;
            for(Block block : blocks){
                if(block.calculateHeatsink(this))somethingChanged = true;
            }
        }while(somethingChanged);
        for(Block block : blocks){//set cell efficiencies
            if(block.isFuelCell()){
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
                if(b.isFuelCellActive()){
                    fuelCells++;
                    cluster.totalOutput+=b.fuel.heat*b.efficiency;
                    cluster.efficiency+=b.efficiency;
                    cluster.totalHeat+=b.moderatorLines*b.fuel.heat;
                    cluster.heatMult+=b.moderatorLines;
                }
                if(b.isHeatsinkActive()){
                    cluster.totalCooling+=b.template.cooling;
                }
                if(b.isShieldActive()){
                    cluster.totalHeat+=b.template.heatMult*b.neutronFlux;
                }
                if(b.isIrradiatorActive()){
                    cluster.irradiation+=b.neutronFlux;
                    if(b.irradiatorRecipe!=null)cluster.totalHeat+=b.irradiatorRecipe.heat*b.neutronFlux;
                }
            }
            cluster.efficiency/=fuelCells;
            cluster.heatMult/=fuelCells;
            if(Double.isNaN(cluster.efficiency))cluster.efficiency = 0;
            if(Double.isNaN(cluster.heatMult))cluster.heatMult = 0;
            cluster.netHeat = cluster.totalHeat-cluster.totalCooling;
            if(cluster.totalCooling==0)cluster.coolingPenaltyMult = 1;
            else cluster.coolingPenaltyMult = Math.min(1, (cluster.totalHeat+getConfiguration().overhaul.fissionSFR.coolingEfficiencyLeniency)/(float)cluster.totalCooling);
            cluster.efficiency*=cluster.coolingPenaltyMult;
            cluster.totalOutput*=cluster.coolingPenaltyMult;
            totalFuelCells+=fuelCells;
            rawOutput+=cluster.totalOutput;
            totalOutput+=cluster.totalOutput;
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
        sparsityMult = (float) (functionalBlocks/(float)volume>=getConfiguration().overhaul.fissionSFR.sparsityPenaltyThreshold?1:getConfiguration().overhaul.fissionSFR.sparsityPenaltyMult+(1-getConfiguration().overhaul.fissionSFR.sparsityPenaltyMult)*Math.sin(Math.PI*functionalBlocks/(2*volume*getConfiguration().overhaul.fissionSFR.sparsityPenaltyThreshold)));
        totalOutput*=sparsityMult;
        totalEfficiency*=sparsityMult;
        totalOutput/=coolantRecipe.heat/coolantRecipe.outputRatio;
        if(!computingShutdown)shutdownFactor = calculateShutdownFactor();
        rainbowScore = getRainbowScore();
    }
    @Override
    protected Block newCasing(int x, int y, int z){
        return new Block(x, y, z, null);
    }
    @Override
    public synchronized String getTooltip(){
        return tooltip(true);
    }
    @Override
    public String getExtraBotTooltip(){
        return tooltip(false);
    }
    public String tooltip(boolean showDetails){
        if(this.showDetails!=null)showDetails = this.showDetails;
        String s = "Total output: "+totalOutput+" mb/t of "+coolantRecipe.output+"\n"
                + "Total Heat: "+totalHeat+"H/t\n"
                + "Total Cooling: "+totalCooling+"H/t\n"
                + "Net Heat: "+netHeat+"H/t\n"
                + "Overall Efficiency: "+percent(totalEfficiency, 0)+"\n"
                + "Overall Heat Multiplier: "+percent(totalHeatMult, 0)+"\n"
                + "Sparsity Penalty Multiplier: "+Math.round(sparsityMult*10000)/10000d+"\n"
                + "Clusters: "+clusters.size()+"\n"
                + "Total Irradiation: "+totalIrradiation+"\n"
                + "Shutdown Factor: "+percent(shutdownFactor, 2)+"\n"
                + "Rainbow Score: "+percent(rainbowScore, 2)+"\n";//TODO make this (and shutdown factor?) modular
        for(Fuel f : getConfiguration().overhaul.fissionSFR.allFuels){
            int i = getFuelCount(f);
            if(i>0)s+="\n"+f.name+": "+i;
        }
        if(showDetails){
            for(Cluster c : clusters){
                s+="\n\n"+c.getTooltip();
            }
        }
        return s;
    }
    @Override
    public int getMultiblockID(){
        return 1;
    }
    @Override
    protected void save(NCPFFile ncpf, Configuration configuration, Config config){
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
                        else blox.add(configuration.overhaul.fissionSFR.allBlocks.indexOf(block.template)+1);
                    }
                }
            }
        }else{
            for(Block block : getBlocks()){
                blox.add(block.x);
                blox.add(block.y);
                blox.add(block.z);
                blox.add(configuration.overhaul.fissionSFR.allBlocks.indexOf(block.template)+1);
            }
        }
        ConfigNumberList fuels = new ConfigNumberList();
        ConfigNumberList sources = new ConfigNumberList();
        ConfigNumberList irradiatorRecipes = new ConfigNumberList();
        for(Block block : getBlocks()){
            if(block.template.fuelCell)fuels.add(configuration.overhaul.fissionSFR.allFuels.indexOf(block.fuel));
            if(block.template.fuelCell)sources.add(configuration.overhaul.fissionSFR.allSources.indexOf(block.source)+1);
            if(block.template.irradiator)irradiatorRecipes.add(configuration.overhaul.fissionSFR.allIrradiatorRecipes.indexOf(block.irradiatorRecipe)+1);
        }
        config.set("blocks", blox);
        config.set("fuels", fuels);
        config.set("sources", sources);
        config.set("irradiatorRecipes", irradiatorRecipes);
        config.set("coolantRecipe", (byte)configuration.overhaul.fissionSFR.allCoolantRecipes.indexOf(coolantRecipe));
    }
    private boolean isCompact(Configuration configuration){
        int blockCount = getBlocks().size();
        int volume = getX()*getY()*getZ();
        int bitsPerDim = logBase(2, Math.max(getX(), Math.max(getY(), getZ())));
        int bitsPerType = logBase(2, configuration.overhaul.fissionSFR.allBlocks.size());
        int compactBits = bitsPerType*volume;
        int spaciousBits = 4*Math.max(bitsPerDim, bitsPerType)*blockCount;
        return compactBits<spaciousBits;
    }
    private static int logBase(int base, int n){
        return (int)(Math.log(n)/Math.log(base));
    }
    @Override
    public void convertTo(Configuration to){
        if(to.overhaul==null||to.overhaul.fissionSFR==null)return;
        for(Block block : getBlocks()){
            if(block.template.fuelCell)block.fuel = to.overhaul.fissionSFR.convert(block.fuel);
            if(block.template.fuelCell)block.source = to.overhaul.fissionSFR.convert(block.source);
            if(block.template.irradiator)block.irradiatorRecipe = to.overhaul.fissionSFR.convert(block.irradiatorRecipe);
            block.template = to.overhaul.fissionSFR.convert(block.template);
        }
        coolantRecipe = to.overhaul.fissionSFR.convert(coolantRecipe);
        setConfiguration(to);
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
    public OverhaulMSR convertToMSR(){
        OverhaulMSR msr = new OverhaulMSR(getX(), getY(), getZ());
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block b = getBlock(x, y, z);
                    msr.setBlockExact(x, y, z, b==null?null:b.convertToMSR());
                }
            }
        }
        msr.metadata.putAll(metadata);
        return msr;
    }
    @Override
    public void addGeneratorSettings(MenuComponentMinimaList multiblockSettings){
        if(fuelToggles==null)fuelToggles = new HashMap<>();
        if(sourceToggles==null)sourceToggles = new HashMap<>();
        if(irradiatorRecipeToggles==null)irradiatorRecipeToggles = new HashMap<>();
        fuelToggles.clear();
        for(Fuel f : getConfiguration().overhaul.fissionSFR.allFuels){
            MenuComponentSFRToggleFuel toggle = new MenuComponentSFRToggleFuel(f);
            fuelToggles.put(f, toggle);
            multiblockSettings.add(toggle);
        }
        for(Source s : getConfiguration().overhaul.fissionSFR.allSources){
            MenuComponentSFRToggleSource toggle = new MenuComponentSFRToggleSource(s);
            sourceToggles.put(s, toggle);
            multiblockSettings.add(toggle);
        }
        for(IrradiatorRecipe r : getConfiguration().overhaul.fissionSFR.allIrradiatorRecipes){
            MenuComponentSFRToggleIrradiatorRecipe toggle = new MenuComponentSFRToggleIrradiatorRecipe(r);
            irradiatorRecipeToggles.put(r, toggle);
            multiblockSettings.add(toggle);
        }
    }
    private HashMap<Fuel, MenuComponentSFRToggleFuel> fuelToggles;
    public ArrayList<Range<Fuel>> validFuels = new ArrayList<>();
    public void setValidFuels(ArrayList<Range<Fuel>> fuels){
        validFuels = fuels;
    }
    public ArrayList<Range<Fuel>> getValidFuels(){
        if(fuelToggles==null){
            return validFuels;
        }
        ArrayList<Range<Fuel>> validFuels = new ArrayList<>();
        for(Fuel f :fuelToggles.keySet()){
            if(fuelToggles.get(f).enabled)validFuels.add(new Range<>(f,fuelToggles.get(f).min,fuelToggles.get(f).max));
        }
        return validFuels;
    }
    private HashMap<Source, MenuComponentSFRToggleSource> sourceToggles;
    public ArrayList<Range<Source>> validSources = new ArrayList<>();
    public void setValidSources(ArrayList<Range<Source>> sources){
        validSources = sources;
    }
    public ArrayList<Range<Source>> getValidSources(){
        if(sourceToggles==null){
            return validSources;
        }
        ArrayList<Range<Source>> validSources = new ArrayList<>();
        for(Source s :sourceToggles.keySet()){
            if(sourceToggles.get(s).enabled)validSources.add(new Range<>(s,sourceToggles.get(s).min,sourceToggles.get(s).max));
        }
        return validSources;
    }
    private HashMap<IrradiatorRecipe, MenuComponentSFRToggleIrradiatorRecipe> irradiatorRecipeToggles;
    public ArrayList<Range<IrradiatorRecipe>> validIrradiatorRecipes = new ArrayList<>();
    public void setValidIrradiatorRecipes(ArrayList<Range<IrradiatorRecipe>> irradiatorRecipes){
        validIrradiatorRecipes = irradiatorRecipes;
    }
    public ArrayList<Range<IrradiatorRecipe>> getValidIrradiatorRecipes(){
        if(irradiatorRecipeToggles==null){
            return validIrradiatorRecipes;
        }
        ArrayList<Range<IrradiatorRecipe>> validIrradiatorRecipes = new ArrayList<>();
        for(IrradiatorRecipe r :irradiatorRecipeToggles.keySet()){
            if(irradiatorRecipeToggles.get(r).enabled)validIrradiatorRecipes.add(new Range<>(r,irradiatorRecipeToggles.get(r).min,irradiatorRecipeToggles.get(r).max));
        }
        return validIrradiatorRecipes;
    }
    private boolean isValid(){
        return totalOutput>0;
    }
    private int getBadCells(){
        int badCells = 0;
        for(Block b : getBlocks()){
            if(b.isFuelCell()&&!b.isFuelCellActive())badCells++;
        }
        return badCells;
    }
    private float calculateShutdownFactor(){
        Stack<Action> copy = future.copy();
        computingShutdown = true;
        action(new SFRAllShieldsAction(true), true);
        float offOut = totalOutput;
        undo();
        computingShutdown = false;
        future = copy;
        return 1-(offOut/totalOutput);
    }
    @Override
    public void getGenerationPriorities(ArrayList<Priority> priorities){
        priorities.add(new Priority<OverhaulSFR>("Valid (>0 output)", true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                if(main.isValid()&&!other.isValid())return 1;
                if(!main.isValid()&&other.isValid())return -1;
                return 0;
            }
        });
        priorities.add(new Priority<OverhaulSFR>("Minimize Bad Cells", true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                return other.getBadCells()-main.getBadCells();
            }
        });
        priorities.add(new Priority<OverhaulSFR>("Shutdownable", true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                return main.shutdownFactor-other.shutdownFactor;
            }
        });
        priorities.add(new Priority<OverhaulSFR>("Stability", false){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                return Math.max(0, other.netHeat)-Math.max(0, main.netHeat);
            }
        });
        priorities.add(new Priority<OverhaulSFR>("Efficiency", true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                return (int) Math.round(main.totalEfficiency*100-other.totalEfficiency*100);
            }
        });
        priorities.add(new Priority<OverhaulSFR>("Output", true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                return main.totalOutput-other.totalOutput;
            }
        });
        priorities.add(new Priority<OverhaulSFR>("Irradiation", true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                return main.totalIrradiation-other.totalIrradiation;
            }
        });
        if(Challenger.isActive){
            priorities.add(new Priority<OverhaulSFR>("Rainbow", false){
                @Override
                protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                    return main.rainbowScore-other.rainbowScore;
                }
            });//TODO make this modular
        }
    }
    @Override
    public void getGenerationPriorityPresets(ArrayList<Priority> priorities, ArrayList<Priority.Preset> presets){
        presets.add(new Priority.Preset("Efficiency", priorities.get(0), priorities.get(1), priorities.get(2), priorities.get(3), priorities.get(4), priorities.get(5), priorities.get(6)).addAlternative("Efficient"));
        presets.add(new Priority.Preset("Output", priorities.get(0), priorities.get(1), priorities.get(2), priorities.get(3), priorities.get(5), priorities.get(4)));
        presets.add(new Priority.Preset("Irradiation", priorities.get(0), priorities.get(1), priorities.get(2), priorities.get(3), priorities.get(6), priorities.get(4), priorities.get(5)).addAlternative("Irradiate").addAlternative("Irradiator"));
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
        if(Challenger.isActive)postProcessingEffects.add(new SmartFillOverhaulSFR());
        for(multiblock.configuration.overhaul.fissionsfr.Block b : getConfiguration().overhaul.fissionSFR.allBlocks){
            if(b.conductor||(b.cluster&&!b.functional))postProcessingEffects.add(new SFRFill(b));
        }
    }
    private float getRainbowScore(){
        float totalSinks = 0;
        for(multiblock.configuration.overhaul.fissionsfr.Block b : getConfiguration().overhaul.fissionSFR.allBlocks){
            if(b.cooling>0)totalSinks++;
        }
        Set<multiblock.configuration.overhaul.fissionsfr.Block> unique = new HashSet<>();
        for(Block b : getBlocks()){
            if(!b.isActive())continue;
            if(b.isHeatsink())unique.add(b.template);
        }
        return unique.size()/totalSinks;
    }
    public class Cluster{
        public ArrayList<Block> blocks = new ArrayList<>();
        public boolean isConnectedToWall = false;
        public float totalOutput = 0;
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
        private Cluster(){}
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
        public boolean contains(int x, int y, int z){
            for(Block b : blocks){
                if(b.x==x&&b.y==y&&b.z==z)return true;
            }
            return false;
        }
        private boolean wallCheck(ArrayList<Block> blocks){
            for(Block block : blocks){
                if(block.x==0||block.y==0||block.z==0)return true;
                if(block.x==getX()-1||block.y==getY()-1||block.z==getZ()-1)return true;
            }
            return false;
        }
        public String getTooltip(){
            return "Total output: "+Math.round(totalOutput)+"\n"
                + "Efficiency: "+percent(efficiency, 0)+"\n"
                + "Total Heating: "+totalHeat+"H/t\n"
                + "Total Cooling: "+totalCooling+"H/t\n"
                + "Net Heating: "+netHeat+"H/t\n"
                + "Heat Multiplier: "+percent(heatMult, 0)+"\n"
                + "Cooling penalty mult: "+Math.round(coolingPenaltyMult*10000)/10000d;
        }
        private Cluster copy(OverhaulSFR newSFR){
            Cluster copy = new Cluster();
            for(Block b : blocks){
                copy.blocks.add(newSFR.getBlock(b.x, b.y, b.z));
            }
            copy.isConnectedToWall = isConnectedToWall;
            copy.totalOutput = totalOutput;
            copy.efficiency = efficiency;
            copy.totalHeat = totalHeat;
            copy.totalCooling = totalCooling;
            copy.netHeat = netHeat;
            copy.heatMult = heatMult;
            copy.coolingPenaltyMult = coolingPenaltyMult;
            copy.irradiation = irradiation;
            return copy;
        }
    }
    @Override
    public void clearData(List<Block> blocks){
        super.clearData(blocks);
        clusters.clear();
        rainbowScore = shutdownFactor = totalOutput = totalEfficiency = totalHeatMult = sparsityMult = totalFuelCells = rawOutput = totalCooling = totalHeat = netHeat = totalIrradiation = functionalBlocks = 0;
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
        return getConfiguration().overhaul!=null&&getConfiguration().overhaul.fissionSFR!=null;
    }
    @Override
    public OverhaulSFR blankCopy(){
        return new OverhaulSFR(getX(), getY(), getZ(), coolantRecipe);
    }
    @Override
    public OverhaulSFR copy(){
        OverhaulSFR copy = blankCopy();
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block get = getBlock(x, y, z);
                    if(get!=null)copy.setBlockExact(x, y, z, get.copy());
                }
            }
        }
        for(Cluster cluster : clusters){
            copy.clusters.add(cluster.copy(copy));
        }
        copy.totalFuelCells = totalFuelCells;
        copy.rawOutput = rawOutput;
        copy.totalOutput = totalOutput;
        copy.totalCooling = totalCooling;
        copy.totalHeat = totalHeat;
        copy.netHeat = netHeat;
        copy.totalEfficiency = totalEfficiency;
        copy.totalHeatMult = totalHeatMult;
        copy.totalIrradiation = totalIrradiation;
        copy.functionalBlocks = functionalBlocks;
        copy.sparsityMult = sparsityMult;
        copy.shutdownFactor = shutdownFactor;
        copy.rainbowScore = rainbowScore;
        return copy;
    }
    @Override
    protected int doCount(Object o){
        int count = 0;
        if(o instanceof Fuel){
            Fuel f = (Fuel)o;
            for(int x = 0; x<getX(); x++){
                for(int y = 0; y<getY(); y++){
                    for(int z = 0; z<getZ(); z++){
                        Block b = getBlock(x, y, z);
                        if(b==null)continue;
                        if(b.fuel==f)count++;
                    }
                }
            }
            return count;
        }
        if(o instanceof Source){
            Source s = (Source)o;
            for(int x = 0; x<getX(); x++){
                for(int y = 0; y<getY(); y++){
                    for(int z = 0; z<getZ(); z++){
                        Block b = getBlock(x, y, z);
                        if(b==null)continue;
                        if(b.source==s)count++;
                    }
                }
            }
            return count;
        }
        if(o instanceof IrradiatorRecipe){
            IrradiatorRecipe r = (IrradiatorRecipe)o;
            for(int x = 0; x<getX(); x++){
                for(int y = 0; y<getY(); y++){
                    for(int z = 0; z<getZ(); z++){
                        Block b = getBlock(x, y, z);
                        if(b==null)continue;
                        if(b.irradiatorRecipe==r)count++;
                    }
                }
            }
            return count;
        }
        throw new IllegalArgumentException("Cannot count "+o.getClass().getName()+" in "+getDefinitionName()+"!");
    }
    @Override
    public String getGeneralName(){
        return "Reactor";
    }
    @Override
    public boolean isCompatible(Multiblock<Block> other){
        return ((OverhaulSFR)other).coolantRecipe==coolantRecipe;
    }
    @Override
    protected void getFluidOutputs(HashMap<String, Double> outputs){
        outputs.put(coolantRecipe.output, (double)totalOutput);
    }
    @Override
    protected void getExtraParts(ArrayList<PartCount> parts){
        int sources = 0;
        for(Source s : getConfiguration().overhaul.fissionSFR.allSources){
            int num = count(s);
            sources+=num;
            if(num>0)parts.add(new PartCount(null, s.name+" Neutron Source", num));
        }
        parts.add(new PartCount(null, "Casing", (getX()+2)*(getZ()+2)*2+(getX()+2)*getY()*2+getY()*getZ()*2-1-sources));
    }
    @Override
    public String getDescriptionTooltip(){
        return "Overhaul SFRs are Solid-Fueled Fission reactors in NuclearCraft: Overhauled\nIf you have blocks called \"Cooler\" instead of \"Heat Sink\", you are playing Underhaul";
    }
}