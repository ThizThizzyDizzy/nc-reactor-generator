package multiblock.overhaul.fissionmsr;
import discord.Bot;
import generator.Priority;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import multiblock.Action;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionmsr.Fuel;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.PartCount;
import multiblock.Range;
import multiblock.ppe.PostProcessingEffect;
import multiblock.action.MSRAllShieldsAction;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.ppe.MSRFill;
import multiblock.symmetry.AxialSymmetry;
import multiblock.symmetry.Symmetry;
import multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe;
import multiblock.configuration.overhaul.fissionmsr.Source;
import multiblock.ppe.ClearInvalid;
import multiblock.ppe.SmartFillOverhaulMSR;
import planner.Core;
import planner.Main;
import planner.file.NCPFFile;
import planner.menu.component.generator.MenuComponentMSRToggleFuel;
import planner.menu.component.generator.MenuComponentMSRToggleSource;
import planner.menu.component.generator.MenuComponentMSRToggleIrradiatorRecipe;
import planner.menu.component.MenuComponentMinimaList;
import simplelibrary.Stack;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigNumberList;
import simplelibrary.opengl.Renderer2D;
public class OverhaulMSR extends Multiblock<Block>{
    public ArrayList<Cluster> clusters = new ArrayList<>();
    private ArrayList<VesselGroup> vesselGroups = new ArrayList<>();
    public int totalFuelVessels;
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
    public float shutdownFactor;
    public float rainbowScore;
    private boolean computingShutdown = false;
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
    public OverhaulMSR newInstance(Configuration configuration){
        OverhaulMSR sfr = new OverhaulMSR();
        sfr.setConfiguration(configuration);
        return sfr;
    }
    @Override
    public Multiblock<Block> newInstance(int x, int y, int z){
        return new OverhaulMSR(x, y, z);
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(getConfiguration()==null||getConfiguration().overhaul==null||getConfiguration().overhaul.fissionMSR==null)return;
        for(multiblock.configuration.overhaul.fissionmsr.Block block : getConfiguration().overhaul.fissionMSR.allBlocks){
            blocks.add(new Block(-1, -1, -1, block));
        }
    }
    @Override
    public int getMinX(){
        return getConfiguration().overhaul.fissionMSR.minSize;
    }
    @Override
    public int getMinY(){
        return getConfiguration().overhaul.fissionMSR.minSize;
    }
    @Override
    public int getMinZ(){
        return getConfiguration().overhaul.fissionMSR.minSize;
    }
    @Override
    public int getMaxX(){
        return getConfiguration().overhaul.fissionMSR.maxSize;
    }
    @Override
    public int getMaxY(){
        return getConfiguration().overhaul.fissionMSR.maxSize;
    }
    @Override
    public int getMaxZ(){
        return getConfiguration().overhaul.fissionMSR.maxSize;
    }
    @Override
    public synchronized void calculate(List<Block> blocks){
        List<Block> allBlocks = getBlocks();
        vesselGroups.clear();
        for(Block b : allBlocks){
            b.vesselGroup = null;
        }
        for(Block block : allBlocks){//detect groups
            VesselGroup group = getVesselGroup(block);
            if(group==null)continue;//that's not a vessel group!
            if(vesselGroups.contains(group))continue;//already know about that one!
            vesselGroups.add(group);
        }
        for(VesselGroup group : vesselGroups){
            if(group.isPrimed()){
                group.propogateNeutronFlux(this);
            }
        }
        int lastActive, nowActive;
        do{
            lastActive = 0;
            for(VesselGroup group : vesselGroups){
                boolean wasActive = group.isActive();
                group.hadFlux = group.neutronFlux;
                group.clearData();
                if(wasActive)lastActive+=group.size();
                group.wasActive = wasActive;
            }
            for(Block block : blocks){
                block.rePropogateNeutronFlux(this);
            }
            nowActive = 0;
            for(VesselGroup group : vesselGroups){
                if(group.isActive())nowActive+=group.size();
                if(!group.wasActive){
                    group.neutronFlux = group.hadFlux;
                }
            }
        }while(nowActive!=lastActive);
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
        for(VesselGroup group : vesselGroups){
            group.positionalEfficiency*=6f*group.size()/group.getOpenFaces();
            for(Block block : group.blocks){
                float criticalityModifier = (float) (1/(1+Math.exp(2*(group.neutronFlux-2*block.vesselGroup.criticality))));
                block.efficiency = block.fuel.efficiency*group.positionalEfficiency*(block.source==null?1:block.source.efficiency)*criticalityModifier;
            }
        }
        for(Block block : allBlocks){//detect clusters
            Cluster cluster = getCluster(block);
            if(cluster==null)continue;//that's not a cluster!
            synchronized(clusters){
                if(clusters.contains(cluster))continue;//already know about that one!
                clusters.add(cluster);
            }
        }
        synchronized(clusters){
            for(Cluster cluster : clusters){
                int fuelVessels = 0;
                for(Block b : cluster.blocks){
                    if(b.isFuelVesselActive()){
                        fuelVessels++;
                        cluster.efficiency+=b.efficiency/b.vesselGroup.size();
                        cluster.totalHeat+=6*(b.vesselGroup.moderatorLines*b.fuel.heat)/b.vesselGroup.getOpenFaces();
                        cluster.heatMult+=b.vesselGroup.getHeatMult()/b.vesselGroup.size();
                    }
                    if(b.isHeaterActive()){
                        cluster.totalCooling+=b.template.cooling;
                    }
                    if(b.isShieldActive()){
                        cluster.totalHeat+=b.template.heatMult*b.flux;
                    }
                    if(b.isIrradiatorActive()){
                        cluster.irradiation+=b.flux;
                        if(b.irradiatorRecipe!=null)cluster.totalHeat+=b.irradiatorRecipe.heat*b.flux;
                    }
                }
                cluster.efficiency/=fuelVessels;
                cluster.heatMult/=fuelVessels;
                if(Double.isNaN(cluster.efficiency))cluster.efficiency = 0;
                if(Double.isNaN(cluster.heatMult))cluster.heatMult = 0;
                cluster.netHeat = cluster.totalHeat-cluster.totalCooling;
                if(cluster.totalCooling==0)cluster.coolingPenaltyMult = 1;
                else cluster.coolingPenaltyMult = Math.min(1, (cluster.totalHeat+getConfiguration().overhaul.fissionMSR.coolingEfficiencyLeniency)/(float)cluster.totalCooling);
                cluster.efficiency*=cluster.coolingPenaltyMult;
                totalFuelVessels+=fuelVessels;
                totalCooling+=cluster.totalCooling;
                totalHeat+=cluster.totalHeat;
                netHeat+=cluster.netHeat;
                totalEfficiency+=cluster.efficiency*fuelVessels;
                totalHeatMult+=cluster.heatMult*fuelVessels;
                totalIrradiation+=cluster.irradiation;
            }
        }
        totalEfficiency/=totalFuelVessels;
        totalHeatMult/=totalFuelVessels;
        if(Double.isNaN(totalEfficiency))totalEfficiency = 0;
        if(Double.isNaN(totalHeatMult))totalHeatMult = 0;
        functionalBlocks = 0;
        for(Block block : allBlocks){
            if(block.isFunctional())functionalBlocks++;
        }
        int volume = getX()*getY()*getZ();
        sparsityMult = (float) (functionalBlocks/(float)volume>=getConfiguration().overhaul.fissionMSR.sparsityPenaltyThreshold?1:getConfiguration().overhaul.fissionMSR.sparsityPenaltyMult+(1-getConfiguration().overhaul.fissionMSR.sparsityPenaltyMult)*Math.sin(Math.PI*functionalBlocks/(2*volume*getConfiguration().overhaul.fissionMSR.sparsityPenaltyThreshold)));
        totalEfficiency*=sparsityMult;
        synchronized(clusters){
            for(Cluster c : clusters){
                for(Block b : c.blocks){
                    if(b.template.cooling!=0){
                        float out = c.efficiency*sparsityMult;
                        totalOutput.put(b.template.output, (totalOutput.containsKey(b.template.output)?totalOutput.get(b.template.output):0)+out);
                        totalTotalOutput+=out;
                    }
                }
            }
        }
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
        String outs = "";
        ArrayList<String> outputList = new ArrayList<>(totalOutput.keySet());
        Collections.sort(outputList);
        for(String s : outputList){
            if(showDetails)outs+="\n "+Math.round(totalOutput.get(s))+" mb/t of "+s;
        }
        synchronized(clusters){
            String s = "Total output: "+Math.round(totalTotalOutput)+" mb/t"+outs+"\n"
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
            for(Fuel f : getConfiguration().overhaul.fissionMSR.allFuels){
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
    }
    @Override
    public int getMultiblockID(){
        return 2;
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
                        else blox.add(configuration.overhaul.fissionMSR.allBlocks.indexOf(block.template)+1);
                    }
                }
            }
        }else{
            for(Block block : getBlocks()){
                blox.add(block.x);
                blox.add(block.y);
                blox.add(block.z);
                blox.add(configuration.overhaul.fissionMSR.allBlocks.indexOf(block.template)+1);
            }
        }
        ConfigNumberList fuels = new ConfigNumberList();
        ConfigNumberList sources = new ConfigNumberList();
        ConfigNumberList irradiatorRecipes = new ConfigNumberList();
        for(Block block : getBlocks()){
            if(block.template.fuelVessel)fuels.add(configuration.overhaul.fissionMSR.allFuels.indexOf(block.fuel));
            if(block.template.fuelVessel)sources.add(configuration.overhaul.fissionMSR.allSources.indexOf(block.source)+1);
            if(block.template.irradiator)irradiatorRecipes.add(configuration.overhaul.fissionMSR.allIrradiatorRecipes.indexOf(block.irradiatorRecipe)+1);
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
        int bitsPerType = logBase(2, configuration.overhaul.fissionMSR.allBlocks.size());
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
            if(block.template.irradiator)block.irradiatorRecipe = to.overhaul.fissionMSR.convert(block.irradiatorRecipe);
            block.template = to.overhaul.fissionMSR.convert(block.template);
        }
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
        synchronized(clusters){
            for(Cluster cluster : clusters){
                if(cluster.contains(block))return cluster;
            }
        }
        return new Cluster(block);
    }
    public VesselGroup getVesselGroup(Block block){
        if(block==null)return null;
        if(!block.isFuelVessel())return null;
        for(VesselGroup vesselGroup : vesselGroups){
            if(vesselGroup.contains(block))return vesselGroup;
        }
        return new VesselGroup(block);
    }
    private int getFuelCount(Fuel f){
        int count = 0;
        for(Block block : getBlocks()){
            if(block.fuel==f)count++;
        }
        return count;
    }
    public OverhaulSFR convertToSFR(){
        OverhaulSFR sfr = new OverhaulSFR(getX(), getY(), getZ(), getConfiguration().overhaul.fissionSFR.allCoolantRecipes.get(0));
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block b = getBlock(x, y, z);
                    sfr.setBlockExact(x, y, z, b==null?null:b.convertToSFR());
                }
            }
        }
        sfr.metadata.putAll(metadata);
        return sfr;
    }
    @Override
    public void addGeneratorSettings(MenuComponentMinimaList multiblockSettings){
        if(fuelToggles==null)fuelToggles = new HashMap<>();
        if(sourceToggles==null)sourceToggles = new HashMap<>();
        if(irradiatorRecipeToggles==null)irradiatorRecipeToggles = new HashMap<>();
        fuelToggles.clear();
        for(Fuel f : getConfiguration().overhaul.fissionMSR.allFuels){
            MenuComponentMSRToggleFuel toggle = new MenuComponentMSRToggleFuel(f);
            fuelToggles.put(f, toggle);
            multiblockSettings.add(toggle);
        }
        sourceToggles.clear();
        for(Source s : getConfiguration().overhaul.fissionMSR.allSources){
            MenuComponentMSRToggleSource toggle = new MenuComponentMSRToggleSource(s);
            sourceToggles.put(s, toggle);
            multiblockSettings.add(toggle);
        }
        irradiatorRecipeToggles.clear();
        for(IrradiatorRecipe r : getConfiguration().overhaul.fissionMSR.allIrradiatorRecipes){
            MenuComponentMSRToggleIrradiatorRecipe toggle = new MenuComponentMSRToggleIrradiatorRecipe(r);
            irradiatorRecipeToggles.put(r, toggle);
            multiblockSettings.add(toggle);
        }
    }
    private HashMap<Fuel, MenuComponentMSRToggleFuel> fuelToggles;
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
    private HashMap<Source, MenuComponentMSRToggleSource> sourceToggles;
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
    private HashMap<IrradiatorRecipe, MenuComponentMSRToggleIrradiatorRecipe> irradiatorRecipeToggles;
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
        return totalTotalOutput>0;
    }
    private int getBadVessels(){
        int badVessels = 0;
        for(Block b : getBlocks()){
            if(b.isFuelVessel()&&!b.isFuelVesselActive())badVessels++;
        }
        return badVessels;
    }
    private float calculateShutdownFactor(){
        Stack<Action> copy = future.copy();
        computingShutdown = true;
        action(new MSRAllShieldsAction(true), true);
        float offOut = totalTotalOutput;
        undo();
        computingShutdown = false;
        future = copy;
        return 1-(offOut/totalTotalOutput);
    }
    @Override
    public void getGenerationPriorities(ArrayList<Priority> priorities){
        priorities.add(new Priority<OverhaulMSR>("Valid (>0 output)", true){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                if(main.isValid()&&!other.isValid())return 1;
                if(!main.isValid()&&other.isValid())return -1;
                return 0;
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Minimize Bad Vessels", true){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return other.getBadVessels()-main.getBadVessels();
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Shutdownable", true){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return main.shutdownFactor-other.shutdownFactor;
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Stability", false){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return Math.max(0, other.netHeat)-Math.max(0, main.netHeat);
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Efficiency", true){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return (int) Math.round(main.totalEfficiency*100-other.totalEfficiency*100);
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Output", true){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return main.totalTotalOutput-other.totalTotalOutput;
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Irradiation", true){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return main.totalIrradiation-other.totalIrradiation;
            }
        });
        priorities.add(new Priority<OverhaulMSR>("Rainbow", false){
            @Override
            protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                return main.rainbowScore-other.rainbowScore;
            }
        });//TODO make this modular
    }
    @Override
    public void getGenerationPriorityPresets(ArrayList<Priority> priorities, ArrayList<Priority.Preset> presets){
        presets.add(new Priority.Preset("Efficiency", priorities.get(0), priorities.get(1), priorities.get(2), priorities.get(3), priorities.get(4), priorities.get(5)).addAlternative("Efficient"));
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
        postProcessingEffects.add(new SmartFillOverhaulMSR());
        for(multiblock.configuration.overhaul.fissionmsr.Block b : getConfiguration().overhaul.fissionMSR.allBlocks){
            if(b.conductor||(b.cluster&&!b.functional))postProcessingEffects.add(new MSRFill(b));
        }
    }
    private float getRainbowScore(){
        float totalSinks = 0;
        for(multiblock.configuration.overhaul.fissionmsr.Block b : getConfiguration().overhaul.fissionMSR.allBlocks){
            if(b.cooling!=0)totalSinks++;
        }
        Set<multiblock.configuration.overhaul.fissionmsr.Block> unique = new HashSet<>();
        for(Block b : getBlocks()){
            if(!b.isActive())continue;
            if(b.isHeater())unique.add(b.template);
        }
        return unique.size()/totalSinks;
    }
    @Override
    protected void getFluidOutputs(HashMap<String, Double> outputs){
        for(String key : totalOutput.keySet()){
            outputs.put(key, (double)totalOutput.get(key));
        }
    }
    public class Cluster{
        public ArrayList<Block> blocks = new ArrayList<>();
        public boolean isConnectedToWall = false;
        public float efficiency;
        public int totalHeat, totalCooling, netHeat;
        public float heatMult, coolingPenaltyMult;
        public int irradiation;
        public Cluster(Block block){
            blocks.addAll(toList(getBlocks(block, false)));
            isConnectedToWall = wallCheck(blocks);
            if(!isConnectedToWall){
                isConnectedToWall = wallCheck(toList(getBlocks(block, true)));
            }
            if(isValid()){
                for(Block b : blocks){
                    b.inCluster = true;
                }
            }
        }
        private Cluster(){}
        private boolean isValid(){
            return isConnectedToWall&&isCreated();
        }
        public boolean isCreated(){
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
        private Cluster copy(OverhaulMSR newMSR){
            Cluster copy = new Cluster();
            for(Block b : blocks){
                copy.blocks.add(newMSR.getBlock(b.x, b.y, b.z));
            }
            copy.isConnectedToWall = isConnectedToWall;
            copy.efficiency = efficiency;
            copy.totalHeat = totalHeat;
            copy.totalCooling = totalCooling;
            copy.netHeat = netHeat;
            copy.heatMult = heatMult;
            copy.coolingPenaltyMult = coolingPenaltyMult;
            copy.irradiation = irradiation;
            return copy;
        }
        /**
         * Block search algorithm from my Tree Feller for Bukkit.
         */
        private HashMap<Integer, ArrayList<Block>> getBlocks(Block start, boolean useConductors){
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
        public boolean contains(int x, int y, int z){
            for(Block b : blocks){
                if(b.x==x&&b.y==y&&b.z==z)return true;
            }
            return false;
        }
    }
    public class VesselGroup{
        public ArrayList<Block> blocks = new ArrayList<>();
        public int criticality = 0;
        public int neutronFlux = 0;
        public int moderatorLines;
        public float positionalEfficiency;
        public int hadFlux;
        public boolean wasActive;
        public VesselGroup(Block block){
            blocks.addAll(toList(getBlocks(block)));
            for(Block b : blocks){
                criticality+=b.fuel.criticality;
                b.vesselGroup = this;
            }
        }
        private VesselGroup(){}
        public boolean contains(Block block){
            return blocks.contains(block);
        }
        /**
         * Block search algorithm from my Tree Feller for Bukkit.
         */
        private HashMap<Integer, ArrayList<Block>> getBlocks(Block start){
            //layer zero
            HashMap<Integer, ArrayList<Block>>results = new HashMap<>();
            ArrayList<Block> zero = new ArrayList<>();
            if(start.isFuelVessel()){
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
                        if(!newBlock.isFuelVessel()||newBlock.fuel!=start.fuel){//that's not part of this bunch
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
        int size(){
            return blocks.size();
        }
        int getOpenFaces(){
            int open = 0;
            for(Block b1 : blocks){
                DIRECTION:for(Direction d : directions){
                    int x = b1.x+d.x;
                    int y = b1.y+d.y;
                    int z = b1.z+d.z;
                    for(Block b2 : blocks){
                        if(b2.x==x&&b2.y==y&&b2.z==z)continue DIRECTION;
                    }
                    open++;
                }
            }
            return open;
        }
        private boolean isActive(){
            return neutronFlux>=criticality;
        }
        private void clearData(){
            for(Block b : blocks)b.clearData();
            wasActive = false;
            neutronFlux = 0;
            positionalEfficiency = 0;
            moderatorLines = 0;
        }
        public float getHeatMult(){
            return 6*size()*((float)moderatorLines)/getOpenFaces();
        }
        public int getRequiredSources(){
            return getOpenFaces()/6;
        }
        public int getSources(){
            int sources = 0;
            for(Block b : blocks){
                if(b.isPrimed())sources++;
            }
            return sources;
        }
        public boolean isPrimed(){
            return getSources()>=getRequiredSources();
        }
        public void propogateNeutronFlux(OverhaulMSR msr){
            for(Block b : blocks){
                b.propogateNeutronFlux(msr);
            }
        }
        public void rePropogateNeutronFlux(OverhaulMSR msr){
            for(Block b : blocks){
                b.rePropogateNeutronFlux(msr);
            }
        }
    }
    @Override
    public synchronized void clearData(List<Block> blocks){
        super.clearData(blocks);
        synchronized(clusters){
            clusters.clear();
        }
        totalOutput.clear();
        rainbowScore = shutdownFactor = totalTotalOutput = totalEfficiency = totalHeatMult = sparsityMult = totalFuelVessels = totalCooling = totalHeat = netHeat = totalIrradiation = functionalBlocks = 0;
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
        return getConfiguration().overhaul!=null&&getConfiguration().overhaul.fissionMSR!=null;
    }
    @Override
    public OverhaulMSR blankCopy(){
        return new OverhaulMSR(getX(), getY(), getZ());
    }
    @Override
    public synchronized OverhaulMSR copy(){
        OverhaulMSR copy = blankCopy();
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block get = getBlock(x, y, z);
                    if(get!=null)copy.setBlockExact(x, y, z, get.copy());
                }
            }
        }
        synchronized(clusters){
            for(Cluster cluster : clusters){
                copy.clusters.add(cluster.copy(copy));
            }
        }
        copy.totalFuelVessels = totalFuelVessels;
        copy.totalCooling = totalCooling;
        copy.totalHeat = totalHeat;
        copy.netHeat = netHeat;
        copy.totalEfficiency = totalEfficiency;
        copy.totalHeatMult = totalHeatMult;
        copy.totalIrradiation = totalIrradiation;
        copy.functionalBlocks = functionalBlocks;
        copy.sparsityMult = sparsityMult;
        copy.totalOutput.putAll(totalOutput);
        copy.totalTotalOutput = totalTotalOutput;
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
        return true;
    }
    @Override
    protected void getExtraParts(ArrayList<PartCount> parts){
        int sources = 0;
        for(Source s : getConfiguration().overhaul.fissionMSR.allSources){
            int num = count(s);
            sources+=num;
            if(num>0){
                Core.BufferRenderer renderer = (buff) -> {
                    float fac = (float) Math.pow(s.efficiency, 10);
                    float r = Math.min(1, -2*fac+2);
                    float g = Math.min(1, fac*2);
                    float b = 0;
                    Core.applyColor(Core.theme.getRGBA(r, g, b, 1));
                    Renderer2D.drawRect(0, 0, buff.width, buff.height, Core.sourceCircle);
                    Core.applyWhite();
                };
                parts.add(new PartCount(Main.isBot?Bot.makeImage(64, 64, renderer):Core.makeImage(64, 64, renderer), s.name+" Neutron Source", num));
            }
        }
        parts.add(new PartCount(null, "Casing", (getX()+2)*(getZ()+2)*2+(getX()+2)*getY()*2+getY()*getZ()*2-1-sources));
    }
    @Override
    public String getDescriptionTooltip(){
        return "Overhaul MSRs are Molten Salt Fission reactors in NuclearCraft: Overhauled";
    }
}