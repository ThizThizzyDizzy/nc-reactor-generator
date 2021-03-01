package multiblock.overhaul.fissionsfr;
import discord.Bot;
import generator.Priority;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import multiblock.Action;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.PartCount;
import multiblock.Range;
import multiblock.action.SFRAllShieldsAction;
import multiblock.action.SetblockAction;
import multiblock.action.SetblocksAction;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import multiblock.configuration.overhaul.fissionsfr.Fuel;
import multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe;
import multiblock.configuration.overhaul.fissionsfr.Source;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.ppe.ClearInvalid;
import multiblock.ppe.PostProcessingEffect;
import multiblock.ppe.SFRFill;
import multiblock.ppe.SmartFillOverhaulSFR;
import multiblock.symmetry.AxialSymmetry;
import multiblock.symmetry.Symmetry;
import planner.Core;
import planner.Core.BufferRenderer;
import planner.FormattedText;
import planner.Main;
import planner.Task;
import planner.module.Module;
import planner.editor.suggestion.Suggestion;
import planner.editor.suggestion.Suggestor;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.generator.MenuComponentSFRToggleFuel;
import planner.menu.component.generator.MenuComponentSFRToggleIrradiatorRecipe;
import planner.menu.component.generator.MenuComponentSFRToggleSource;
import simplelibrary.Stack;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigNumberList;
import simplelibrary.opengl.Renderer2D;
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
    private int calculationStep = 0;//0 is initial calculation, 1 is shield check, 2 is shutdown factor check
    private ArrayList<Block> cellsWereActive = new ArrayList<>();//used for shield check
    public OverhaulSFR(){
        this(null);
    }
    public OverhaulSFR(Configuration configuration){
        this(configuration, 7, 5, 7, null);
    }
    public OverhaulSFR(Configuration configuration, int x, int y, int z, CoolantRecipe coolantRecipe){
        super(configuration, x, y, z);
        this.coolantRecipe = coolantRecipe==null?(exists()?getConfiguration().overhaul.fissionSFR.allCoolantRecipes.get(0):null):coolantRecipe;
    }
    @Override
    public String getDefinitionName(){
        return "Overhaul SFR";
    }
    @Override
    public OverhaulSFR newInstance(Configuration configuration){
        return new OverhaulSFR(configuration);
    }
    @Override
    public Multiblock<Block> newInstance(Configuration configuration, int x, int y, int z){
        return new OverhaulSFR(configuration, x, y, z, null);
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(getConfiguration()==null||getConfiguration().overhaul==null||getConfiguration().overhaul.fissionSFR==null)return;
        for(multiblock.configuration.overhaul.fissionsfr.Block block : getConfiguration().overhaul.fissionSFR.allBlocks){
            blocks.add(new Block(getConfiguration(), -1, -1, -1, block));
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
    public void doCalculate(List<Block> blocks){
        Task propogateFlux = new Task("Propogating Neutron Flux");
        Task rePropogateFlux = new Task("Re-propogating Neutron Flux");
        Task postFluxCalc = new Task("Performing Post-Flux Calculations");
        Task calcHeatsinks = new Task("Calculating Heatsinks");
        Task buildClusters = new Task("Building Clusters");
        Task calcClusters = new Task("Calculating Clusters");
        Task calcStats = new Task("Calculating Stats");
        Task calcPartialShutdown = new Task("Calculating Partial Shutdown");
        Task calcShutdown = new Task("Calculating Shutdown Factor");
        switch(calculationStep){
            case 0:
                calculateTask.addSubtask(propogateFlux);
                calculateTask.addSubtask(rePropogateFlux);
                calculateTask.addSubtask(postFluxCalc);
                calculateTask.addSubtask(calcHeatsinks);
                calculateTask.addSubtask(buildClusters);
                calculateTask.addSubtask(calcClusters);
                calculateTask.addSubtask(calcStats);
                break;
            case 1:
                calcPartialShutdown.addSubtask(propogateFlux);
                calcPartialShutdown.addSubtask(rePropogateFlux);
                calcPartialShutdown.addSubtask(postFluxCalc);
                calcPartialShutdown.addSubtask(calcHeatsinks);
                calcPartialShutdown.addSubtask(buildClusters);
                calcPartialShutdown.addSubtask(calcClusters);
                calcPartialShutdown.addSubtask(calcStats);
                calculateTask.addSubtask(calcPartialShutdown);
                break;
            case 2:
                calcShutdown.addSubtask(propogateFlux);
                calcShutdown.addSubtask(rePropogateFlux);
                calcShutdown.addSubtask(postFluxCalc);
                calcShutdown.addSubtask(calcHeatsinks);
                calcShutdown.addSubtask(buildClusters);
                calcShutdown.addSubtask(calcClusters);
                calcShutdown.addSubtask(calcStats);
                calculateTask.addSubtask(calcShutdown);
                break;
        }
        HashMap<Block, Boolean> shieldsWere = new HashMap<>();
        List<Block> allBlocks = getBlocks();
        if(calculationStep!=1){//temporarily open all shields
            for(Block block : allBlocks){
                shieldsWere.put(block, block.closed);
                block.closed = false;
            }
        }
        for(int i = 0; i<blocks.size(); i++){
            Block block = blocks.get(i);
            block.propogateNeutronFlux(this, calculationStep==1&&cellsWereActive.contains(block));
            propogateFlux.progress = i/(double)blocks.size();
        }
        propogateFlux.finish();
        int lastActive, nowActive;
        int n = 0;
        do{
            n++;
            rePropogateFlux.name = "Re-propogating Neutron Flux"+(n>1?" ("+n+")":"");
            lastActive = 0;
            for(Block block : blocks){
                boolean wasActive = block.isFuelCellActive();
                block.hadFlux = block.neutronFlux;
                block.clearData();
                if(wasActive)lastActive++;
                block.wasActive = wasActive;
            }
            for(int i = 0; i<blocks.size(); i++){
                Block block = blocks.get(i);
                block.rePropogateNeutronFlux(this, calculationStep==1&&cellsWereActive.contains(block));
                rePropogateFlux.progress = i/(double)blocks.size();
            }
            nowActive = 0;
            for(Block block : blocks){
                if(block.isFuelCellActive())nowActive++;
                if(block.isFuelCell()&&!block.wasActive){
                    block.neutronFlux = block.hadFlux;
                }
            }
        }while(nowActive!=lastActive);
        rePropogateFlux.finish();
        for(int i = 0; i<blocks.size(); i++){
            Block block = blocks.get(i);
            if(block.isFuelCell())block.postFluxCalc(this);
            postFluxCalc.progress = i/(double)blocks.size();
        }
        postFluxCalc.finish();
        boolean somethingChanged;
        n = 0;
        do{
            somethingChanged = false;
            n++;
            calcHeatsinks.name = "Calculating Heatsinks"+(n>1?" ("+n+")":"");
            for(int i = 0; i<blocks.size(); i++){
                if(blocks.get(i).calculateHeatsink(this))somethingChanged = true;
                calcHeatsinks.progress = i/(double)blocks.size();
            }
        }while(somethingChanged);
        calcHeatsinks.finish();
        for(Block block : blocks){//set cell efficiencies
            if(block.isFuelCell()){
                float criticalityModifier = (float) (1/(1+Math.exp(2*(block.neutronFlux-2*block.fuel.criticality))));
                block.efficiency = block.fuel.efficiency*block.positionalEfficiency*(block.source==null?1:block.source.efficiency)*criticalityModifier;
            }
        }
        for(int i = 0; i<allBlocks.size(); i++){//detect clusters
            Cluster cluster = getCluster(allBlocks.get(i));
            if(cluster==null)continue;//that's not a cluster!
            synchronized(clusters){
                if(clusters.contains(cluster))continue;//already know about that one!
                clusters.add(cluster);
            }
            buildClusters.progress = i/(double)allBlocks.size();
        }
        buildClusters.finish();
        synchronized(clusters){
            for(int i = 0; i<clusters.size(); i++){
                Cluster cluster = clusters.get(i);
                int fuelCells = 0;
                for(int j = 0; j<cluster.blocks.size(); j++){
                    Block b = cluster.blocks.get(j);
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
                        cluster.totalOutput+=b.template.heatMult*b.neutronFlux*b.template.efficiency;
                        cluster.totalHeat+=b.template.heatMult*b.neutronFlux;
                    }
                    if(b.isIrradiatorActive()){
                        cluster.irradiation+=b.neutronFlux;
                        if(b.irradiatorRecipe!=null)cluster.totalHeat+=b.irradiatorRecipe.heat*b.neutronFlux;
                    }
                    calcClusters.progress = (i+j/(double)cluster.blocks.size())/(double)clusters.size();
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
                calcClusters.progress = (i+1)/(double)clusters.size();
            }
        }
        calcClusters.finish();
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
        calcStats.finish();
        for(Block b : shieldsWere.keySet()){
            b.closed = shieldsWere.get(b);
        }
        if(calculationStep!=1){
            calculatePartialShutdown();
        }
        calcPartialShutdown.finish();
        if(calculationStep==0){
            shutdownFactor = calculateShutdownFactor();
        }
        calcShutdown.finish();
    }
    private void calculatePartialShutdown(){
        int last = calculationStep;
        calculationStep = 1;
        cellsWereActive.clear();
        for(Block b : getBlocks())if(b!=null&&b.isFuelCellActive())cellsWereActive.add(b);
        recalculate();
        calculationStep = last;
    }
    private float calculateShutdownFactor(){
        Stack<Action> copy = future.copy();
        calculationStep = 2;
        action(new SFRAllShieldsAction(true), true);
        float offOut = totalOutput;
        undo();
        calculationStep = 0;
        future = copy;
        return 1-(offOut/totalOutput);
    }
    @Override
    protected Block newCasing(int x, int y, int z){
        return new Block(getConfiguration(), x, y, z, null);
    }
    @Override
    public synchronized FormattedText getTooltip(){
        return tooltip(true);
    }
    @Override
    public String getExtraBotTooltip(){
        return tooltip(false).text;
    }
    public FormattedText tooltip(boolean showDetails){
        if(this.showDetails!=null)showDetails = this.showDetails;
        synchronized(clusters){
            int validClusters = 0;
            for(Cluster c : clusters){
                if(c.isValid())validClusters++;
            }
            FormattedText text = new FormattedText("Total output: "+totalOutput+" mb/t of "+coolantRecipe.output+"\n"
                    + "Total Heat: "+totalHeat+"H/t\n"
                    + "Total Cooling: "+totalCooling+"H/t\n"
                    + "Net Heat: "+netHeat+"H/t\n"
                    + "Overall Efficiency: "+percent(totalEfficiency, 0)+"\n"
                    + "Overall Heat Multiplier: "+percent(totalHeatMult, 0)+"\n"
                    + "Sparsity Penalty Multiplier: "+Math.round(sparsityMult*10000)/10000d+"\n"
                    + "Clusters: "+(validClusters==clusters.size()?clusters.size():(validClusters+"/"+clusters.size()))+"\n"
                    + "Total Irradiation: "+totalIrradiation+"\n"
                    + "Shutdown Factor: "+percent(shutdownFactor, 2));
            text.addText(getModuleTooltip()+"\n");
            for(Fuel f : getConfiguration().overhaul.fissionSFR.allFuels){
                int i = getFuelCount(f);
                if(i>0)text.addText("\n"+f.name+": "+i);
            }
            if(showDetails){
                HashMap<String, Integer> counts = new HashMap<>();
                HashMap<String, Color> colors = new HashMap<>();
                ArrayList<String> order = new ArrayList<>();
                for(Cluster c : clusters){
                    String str = c.getTooltip();
                    if(counts.containsKey(str)){
                        counts.put(str, counts.get(str)+1);
                    }else{
                        counts.put(str, 1);
                        order.add(str);
                    }
                    if(!c.isCreated()){
                        colors.put(str, Core.theme.getRGBA(Color.white));
                    }else if(!c.isConnectedToWall){
                        colors.put(str, Core.theme.getRGBA(Color.pink));
                    }else if(c.netHeat>0)colors.put(str, Core.theme.getRed());
                    else if(c.coolingPenaltyMult!=1)colors.put(str, Core.theme.getBlue());
                }
                for(String str : order){
                    int count = counts.get(str);
                    String s;
                    if(count==1)s="\n\n"+str;
                    else{
                        s="\n\n"+count+" similar clusters:\n\n"+str;
                    }
                    text.addText(s, colors.get(str));
                }
            }
            return text;
        }
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
            block.convertTo(to);
        }
        coolantRecipe = to.overhaul.fissionSFR.convert(coolantRecipe);
        configuration = to;
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
    public int getFuelCount(Fuel f){
        int count = 0;
        for(Block block : getBlocks()){
            if(block.fuel==f)count++;
        }
        return count;
    }
    public HashMap<Fuel, Integer> getFuelCounts(){
        HashMap<Fuel, Integer> counts = new HashMap<>();
        for(Fuel f : getConfiguration().overhaul.fissionSFR.allFuels){
            int count = getFuelCount(f);
            if(count!=0)counts.put(f, count);
        }
        return counts;
    }
    public OverhaulMSR convertToMSR(){
        OverhaulMSR msr = new OverhaulMSR(configuration, getX(), getY(), getZ());
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
    @Override
    public void getGenerationPriorities(ArrayList<Priority> priorities){
        priorities.add(new Priority<OverhaulSFR>("Valid (>0 output)", true, true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                if(main.isValid()&&!other.isValid())return 1;
                if(!main.isValid()&&other.isValid())return -1;
                return 0;
            }
        });
        priorities.add(new Priority<OverhaulSFR>("Minimize Bad Cells", true, true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                return other.getBadCells()-main.getBadCells();
            }
        });
        priorities.add(new Priority<OverhaulSFR>("Shutdownable", true, true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                return main.shutdownFactor-other.shutdownFactor;
            }
        });
        priorities.add(new Priority<OverhaulSFR>("Stability", false, true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                return Math.max(0, other.netHeat)-Math.max(0, main.netHeat);
            }
        });
        priorities.add(new Priority<OverhaulSFR>("Efficiency", true, true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                return (int) Math.round(main.totalEfficiency*100-other.totalEfficiency*100);
            }
        });
        priorities.add(new Priority<OverhaulSFR>("Output", true, true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                return main.totalOutput-other.totalOutput;
            }
        });
        priorities.add(new Priority<OverhaulSFR>("Irradiation", true, true){
            @Override
            protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                return main.totalIrradiation-other.totalIrradiation;
            }
        });
        for(Module m : Core.modules){
            if(m.isActive())m.getGenerationPriorities(this, priorities);
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
        postProcessingEffects.add(new SmartFillOverhaulSFR());
        for(multiblock.configuration.overhaul.fissionsfr.Block b : getConfiguration().overhaul.fissionSFR.allBlocks){
            if(b.conductor||(b.cluster&&!b.functional))postProcessingEffects.add(new SFRFill(b));
        }
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
            for(Block b : blocks){
                b.cluster = this;
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
            if(!isCreated())return "Invalid cluster!";
            if(!isValid())return "Cluster is not connected to the casing!";
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
        synchronized(clusters){
            clusters.clear();
        }
        shutdownFactor = totalOutput = totalEfficiency = totalHeatMult = sparsityMult = totalFuelCells = rawOutput = totalCooling = totalHeat = netHeat = totalIrradiation = functionalBlocks = 0;
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
        return super.exists()&&getConfiguration().overhaul!=null&&getConfiguration().overhaul.fissionSFR!=null;
    }
    @Override
    public OverhaulSFR blankCopy(){
        return new OverhaulSFR(configuration, getX(), getY(), getZ(), coolantRecipe);
    }
    @Override
    public OverhaulSFR doCopy(){
        OverhaulSFR copy = blankCopy();
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
            if(num>0){
                BufferRenderer renderer = (buff) -> {
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
        return "Overhaul SFRs are Solid-Fueled Fission reactors in NuclearCraft: Overhauled\nIf you have blocks called \"Cooler\" instead of \"Heat Sink\", you are playing Underhaul";
    }
    @Override
    public void getSuggestors(ArrayList<Suggestor> suggestors){
        suggestors.add(new Suggestor<OverhaulSFR>("Fuel Cell Suggestor", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulSFR>("Efficiency", true, true){
                    @Override
                    protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                        return main.totalEfficiency-other.totalEfficiency;
                    }
                });
                priorities.add(new Priority<OverhaulSFR>("Output", true, true){
                    @Override
                    protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                        return main.totalOutput-other.totalOutput;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests adding Fuel cells with moderators to increase efficiency and output";
            }
            @Override
            public void generateSuggestions(OverhaulSFR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> cells = new ArrayList<>();
                multiblock.getAvailableBlocks(cells);
                for(Iterator<Block> it = cells.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isFuelCell())it.remove();
                }
                ArrayList<Block> moderators = new ArrayList<>();
                multiblock.getAvailableBlocks(moderators);
                for(Iterator<Block> it = moderators.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isModerator())it.remove();
                }
                HashSet<Fuel> fuels = new HashSet<>();
                int cellCount = 0;
                for(int y = 0; y<multiblock.getY(); y++){
                    for(int z = 0; z<multiblock.getZ(); z++){
                        for(int x = 0; x<multiblock.getX(); x++){
                            Block b = multiblock.getBlock(x, y, z);
                            if(b!=null&&b.isFuelCell())cellCount++;
                            if(b!=null&&b.fuel!=null)fuels.add(b.fuel);
                        }
                    }
                }
                suggestor.setCount((multiblock.getX()*multiblock.getY()*multiblock.getZ()-cellCount)*cells.size()*moderators.size());
                for(Block cell : cells){
                    for(Block moderator : moderators){
                        for(int y = 0; y<multiblock.getY(); y++){
                            for(int z = 0; z<multiblock.getZ(); z++){
                                for(int x = 0; x<multiblock.getX(); x++){
                                    Block was = multiblock.getBlock(x, y, z);
                                    if(was!=null&&was.isFuelCell())continue;
                                    for(Fuel fuel : fuels){
                                        ArrayList<Action> actions = new ArrayList<>();
                                        Block ce = (Block)cell.newInstance(x, y, z);
                                        ce.fuel = fuel;
                                        actions.add(new SetblockAction(x, y, z, ce));
                                        SetblocksAction multi = new SetblocksAction(moderator);
                                        DIRECTION:for(Direction d : directions){
                                            ArrayList<int[]> toSet = new ArrayList<>();
                                            boolean yep = false;
                                            for(int i = 1; i<=configuration.overhaul.fissionSFR.neutronReach+1; i++){
                                                int X = x+d.x*i;
                                                int Y = y+d.y*i;
                                                int Z = z+d.z*i;
                                                Block b = multiblock.getBlock(X, Y, Z);
                                                if(b!=null){
                                                    if(b.isCasing())break;//end of the line
                                                    if(b.isModerator())continue;//already a moderator
                                                    if(b.isFuelCell()){
                                                        yep = true;
                                                        break;
                                                    }
                                                }
                                                if(i<=configuration.overhaul.fissionSFR.neutronReach){
                                                    toSet.add(new int[]{X,Y,Z});
                                                }
                                            }
                                            if(yep){
                                                for(int[] b : toSet)multi.add(b[0], b[1], b[2]);
                                            }
                                        }
                                        if(!multi.isEmpty())actions.add(multi);
                                        if(suggestor.acceptingSuggestions())suggestor.suggest(new Suggestion("Add "+cell.getName()+(multi.isEmpty()?"":" with "+moderator.getName()), actions, priorities));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        suggestors.add(new Suggestor<OverhaulSFR>("Moderator Line Upgrader", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulSFR>("Efficiency", true, true){
                    @Override
                    protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                        return (int) Math.round(main.totalEfficiency*100-other.totalEfficiency*100);
                    }
                });
                priorities.add(new Priority<OverhaulSFR>("Irradiation", true, true){
                    @Override
                    protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                        return main.totalIrradiation-other.totalIrradiation;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests changing moderator lines to increase efficiency or irradiation";
            }
            @Override
            public void generateSuggestions(OverhaulSFR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> moderators = new ArrayList<>();
                multiblock.getAvailableBlocks(moderators);
                for(Iterator<Block> it = moderators.iterator(); it.hasNext();){
                    Block block = it.next();
                    if(!block.isModerator()||block.template.flux<=0)it.remove();
                }
                int count = 0;
                for(Block block : multiblock.getBlocks()){
                    if(block.isFuelCell())count++;
                }
                suggestor.setCount(count*6*moderators.size());
                for(Block block : multiblock.getBlocks()){
                    if(!block.isFuelCell())continue;
                    DIRECTION:for(Direction d : directions){
                        ArrayList<Block> line = new ArrayList<>();
                        int x = block.x;
                        int y = block.y;
                        int z = block.z;
                        for(int i = 0; i<getConfiguration().overhaul.fissionSFR.neutronReach+1; i++){
                            x+=d.x;
                            y+=d.y;
                            z+=d.z;
                            Block b = multiblock.getBlock(x, y, z);
                            if(b==null){
                                suggestor.task.max--;
                                continue DIRECTION;
                            }
                            if(!b.isModerator()){
                                if(b.isFuelCell()||b.isIrradiator()||b.isReflector())break;
                                suggestor.task.max--;
                                continue DIRECTION;
                            }
                            line.add(b);
                        }
                        if(line.size()>getConfiguration().overhaul.fissionSFR.neutronReach){
                            suggestor.task.max--;
                            continue;
                        }//too long
                        for(Block mod : moderators){
                            ArrayList<Action> actions = new ArrayList<>();
                            for(Block b : line){
                                actions.add(new SetblockAction(b.x, b.y, b.z, mod.newInstance(b.x, b.y, b.z)));
                            }
                            suggestor.suggest(new Suggestion("Replace Moderator Line with "+mod.getName().replace(" Moderator", ""), actions, priorities));
                        }
                    }
                }
            }
        });
        suggestors.add(new Suggestor<OverhaulSFR>("Single Moderator Upgrader", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulSFR>("Efficiency", true, true){
                    @Override
                    protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                        return (int) Math.round(main.totalEfficiency*100-other.totalEfficiency*100);
                    }
                });
                priorities.add(new Priority<OverhaulSFR>("Irradiation", true, true){
                    @Override
                    protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                        return main.totalIrradiation-other.totalIrradiation;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests changing single moderators to increase efficiency or irradiation";
            }
            @Override
            public void generateSuggestions(OverhaulSFR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> blocks = new ArrayList<>();
                multiblock.getAvailableBlocks(blocks);
                for(Iterator<Block> it = blocks.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isModerator()||b.template.flux<=0)it.remove();
                }
                int count = 0;
                for(Block b : multiblock.getBlocks()){
                    if(b.isModerator())count++;
                }
                suggestor.setCount(count*blocks.size());
                for(Block block : multiblock.getBlocks()){
                    if(!block.isModerator())continue;
                    for(Block b : blocks){
                        suggestor.suggest(new Suggestion("Upgrade Moderator from "+block.getName().replace(" Moderator", "")+" to "+b.getName().replace(" Moderator", ""), new SetblockAction(block.x, block.y, block.z, b.newInstance(block.x, block.y, block.z)), priorities));
                    }
                }
            }
        });
        suggestors.add(new Suggestor<OverhaulSFR>("Heatsink Suggestor", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulSFR>("Temperature", true, true){
                    @Override
                    protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                        return other.netHeat-main.netHeat;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests adding or replacing heat sinks to cool the reactor";
            }
            @Override
            public void generateSuggestions(OverhaulSFR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> blocks = new ArrayList<>();
                multiblock.getAvailableBlocks(blocks);
                for(Iterator<Block> it = blocks.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isHeatsink())it.remove();
                }
                int count = 0;
                for(int x = 0; x<multiblock.getX(); x++){
                    for(int y = 0; y<multiblock.getY(); y++){
                        for(int z = 0; z<multiblock.getZ(); z++){
                            Block block = multiblock.getBlock(x, y, z);
                            if(block==null||block.canBeQuickReplaced()){
                                count++;
                            }
                        }
                    }
                }
                suggestor.setCount(count*blocks.size());
                for(int x = 0; x<multiblock.getX(); x++){
                    for(int y = 0; y<multiblock.getY(); y++){
                        for(int z = 0; z<multiblock.getZ(); z++){
                            for(Block newBlock : blocks){
                                Block block = multiblock.getBlock(x, y, z);
                                if(block==null||block.canBeQuickReplaced()){
                                    if(newBlock.template.cooling>(block==null?0:block.template.cooling)&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock.newInstance(x, y, z)), priorities));
                                    else suggestor.task.max--;
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}