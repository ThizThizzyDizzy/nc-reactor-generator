package net.ncplanner.plannerator.multiblock.overhaul.fissionmsr;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.FluidStack;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.PartCount;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.multiblock.editor.action.SetblockAction;
import net.ncplanner.plannerator.multiblock.editor.action.SetblocksAction;
import net.ncplanner.plannerator.multiblock.editor.decal.AdjacentModeratorLineDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.BlockInvalidDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.BlockValidDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.CellFluxDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.IrradiatorAdjacentModeratorLineDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.MissingCasingDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.ModeratorActiveDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.NeutronSourceDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.NeutronSourceLineDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.NeutronSourceNoTargetDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.NeutronSourceTargetDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.OverhaulModeratorLineDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.ReflectorAdjacentModeratorLineDecal;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.Queue;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestion;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulMSRDesign;
public class OverhaulMSR extends CuboidalMultiblock<Block>{
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
    public ArrayList<FluidStack> totalOutput = new ArrayList<>();
    public float totalTotalOutput;
    public float shutdownFactor;
    private HashMap<Block, Boolean> shieldsWere = new HashMap<>();//used for shield check
    private ArrayList<VesselGroup> vesselGroupsWereActive = new ArrayList<>();//used for shield check
    private ArrayList<Block> vesselsWereActive = new ArrayList<>();//used for shield check
    private HashMap<NCPFElement, BlockElement> missingInputPorts = new HashMap<>();
    private HashMap<NCPFElement, BlockElement> missingOutputPorts = new HashMap<>();
    private int calcStep = 0;
    private int calcSubstep = 0;
    private int numControllers;
    private int missingCasings;
    private Task calcCasing;
    private Task openShields;
    private Task buildGroups;
    private Task propogateFlux;
    private Task rePropogateFlux;
    private Task postFluxCalc;
    private Task calcHeaters;
    private Task initVessels;
    private Task buildClusters;
    private Task calcClusters;
    private Task calcStats;
    private Task calcShutdown;
    private Task shutdownCalcCasing;
    private Task shutdownCloseShields;
    private Task shutdownBuildGroups;
    private Task shutdownPropogateFlux;
    private Task shutdownRePropogateFlux;
    private Task shutdownPostFluxCalc;
    private Task shutdownCalcHeaters;
    private Task shutdownInitVessels;
    private Task shutdownBuildClusters;
    private Task shutdownCalcClusters;
    private Task shutdownCalcStats;
    private Task calcPartialShutdown;
    private Task partialShutdownCalcCasing;
    private Task partialShutdownResetShields;
    private Task partialShutdownBuildGroups;
    private Task partialShutdownPropogateFlux;
    private Task partialShutdownRePropogateFlux;
    private Task partialShutdownPostFluxCalc;
    private Task partialShutdownCalcHeaters;
    private Task partialShutdownInitVessels;
    private Task partialShutdownBuildClusters;
    private Task partialShutdownCalcClusters;
    private Task partialShutdownCalcStats;
    private float offOutput;
    public final Queue<Decal> initialFluxDecals = new Queue<>();
    public final Queue<Decal> finalFluxDecals = new Queue<>();
    public final Queue<Decal> shutdownFluxDecals = new Queue<>();
    public OverhaulMSR(){
        this(null);
    }
    public OverhaulMSR(NCPFConfigurationContainer configuration){
        this(configuration, 7, 5, 7);
    }
    public OverhaulMSR(NCPFConfigurationContainer configuration, int x, int y, int z){
        super(configuration, x, y, z);
    }
    @Override
    public OverhaulMSRConfiguration getSpecificConfiguration(){
        NCPFConfigurationContainer conf = getConfiguration();
        if(conf==null)return null;
        return conf.getConfiguration(OverhaulMSRConfiguration::new);
    }
    @Override
    public String getDefinitionName(){
        return "Overhaul MSR";
    }
    @Override
    public OverhaulMSR newInstance(NCPFConfigurationContainer configuration){
        return new OverhaulMSR(configuration);
    }
    @Override
    public Multiblock<Block> newInstance(NCPFConfigurationContainer configuration, int x, int y, int z){
        return new OverhaulMSR(configuration, x, y, z);
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(getSpecificConfiguration()==null)return;
        for(BlockElement block : getSpecificConfiguration().blocks){
            blocks.add(new Block(getConfiguration(), -1, -1, -1, block));
        }
    }
    @Override
    public int getMinX(){
        return getSpecificConfiguration().settings.minSize;
    }
    @Override
    public int getMinY(){
        return getSpecificConfiguration().settings.minSize;
    }
    @Override
    public int getMinZ(){
        return getSpecificConfiguration().settings.minSize;
    }
    @Override
    public int getMaxX(){
        return getSpecificConfiguration().settings.maxSize;
    }
    @Override
    public int getMaxY(){
        return getSpecificConfiguration().settings.maxSize;
    }
    @Override
    public int getMaxZ(){
        return getSpecificConfiguration().settings.maxSize;
    }
    @Override
    public void genCalcSubtasks(){
        calcCasing = calculateTask.addSubtask(new Task("Calculating Casing"));
        openShields = calculateTask.addSubtask(new Task("Opening Neutron Shields"));
        buildGroups = calculateTask.addSubtask(new Task("Building Vessel Groups"));
        propogateFlux = calculateTask.addSubtask(new Task("Propogating Neutron Flux"));
        rePropogateFlux = calculateTask.addSubtask(new Task("Re-propogating Neutron Flux"));
        postFluxCalc = calculateTask.addSubtask(new Task("Performing Post-Flux Calculations"));
        calcHeaters = calculateTask.addSubtask(new Task("Calculating Heaters"));
        initVessels = calculateTask.addSubtask(new Task("Initializing Fuel Vessels"));
        buildClusters = calculateTask.addSubtask(new Task("Building Clusters"));
        calcClusters = calculateTask.addSubtask(new Task("Calculating Clusters"));
        calcStats = calculateTask.addSubtask(new Task("Calculating Stats"));
        calcShutdown = calculateTask.addSubtask(new Task("Calculating Shutdown Factor"));
        shutdownCalcCasing = calculateTask.addSubtask(new Task("Calculating Casing"));
        shutdownCloseShields = calcShutdown.addSubtask(new Task("Closing Neutron Shields"));
        shutdownBuildGroups = calcShutdown.addSubtask(new Task("Building Vessel Groups"));
        shutdownPropogateFlux = calcShutdown.addSubtask(new Task("Propogating Neutron Flux"));
        shutdownRePropogateFlux = calcShutdown.addSubtask(new Task("Re-propogating Neutron Flux"));
        shutdownPostFluxCalc = calcShutdown.addSubtask(new Task("Performing Post-Flux Calculations"));
        shutdownCalcHeaters = calcShutdown.addSubtask(new Task("Calculating Heaters"));
        shutdownInitVessels = calcShutdown.addSubtask(new Task("Initializing Fuel Vessels"));
        shutdownBuildClusters = calcShutdown.addSubtask(new Task("Building Clusters"));
        shutdownCalcClusters = calcShutdown.addSubtask(new Task("Calculating Clusters"));
        shutdownCalcStats = calcShutdown.addSubtask(new Task("Calculating Stats"));
        calcPartialShutdown = calculateTask.addSubtask(new Task("Calculating Partial Shutdown"));
        partialShutdownCalcCasing = calculateTask.addSubtask(new Task("Calculating Casing"));
        partialShutdownResetShields = calcPartialShutdown.addSubtask(new Task("Resetting Neutron Shields"));
        partialShutdownBuildGroups = calcPartialShutdown.addSubtask(new Task("Building Vessel Groups"));
        partialShutdownPropogateFlux = calcPartialShutdown.addSubtask(new Task("Propogating Neutron Flux"));
        partialShutdownRePropogateFlux = calcPartialShutdown.addSubtask(new Task("Re-propogating Neutron Flux"));
        partialShutdownPostFluxCalc = calcPartialShutdown.addSubtask(new Task("Performing Post-Flux Calculations"));
        partialShutdownCalcHeaters = calcPartialShutdown.addSubtask(new Task("Calculating Heaters"));
        partialShutdownInitVessels = calcPartialShutdown.addSubtask(new Task("Initializing Fuel Vessels"));
        partialShutdownBuildClusters = calcPartialShutdown.addSubtask(new Task("Building Clusters"));
        partialShutdownCalcClusters = calcPartialShutdown.addSubtask(new Task("Calculating Clusters"));
        partialShutdownCalcStats = calcPartialShutdown.addSubtask(new Task("Calculating Stats"));
    }
    @Override
    public boolean doCalculationStep(List<Block> blocks, boolean addDecals){
        List<Block> allBlocks = getBlocks();
        switch(calcStep){
            //<editor-fold defaultstate="collapsed" desc="Base calculations">
            case 0://calculate casing
                initialFluxDecals.clear();
                finalFluxDecals.clear();
                shutdownFluxDecals.clear();
                numControllers = missingCasings = 0;
                missingInputPorts.clear();
                missingOutputPorts.clear();
                forEachCasingPosition((x, y, z) -> {
                    Block block = getBlock(x, y, z);
                    if(block==null||!block.isCasing()){
                        missingCasings++;
                        if(addDecals)decals.enqueue(new MissingCasingDecal(x, y, z));
                    }
                    if(block!=null&&block.isCasing()){
                        if(block.isController()){
                            numControllers++;
                        }
                        if(block.template.neutronSource!=null){
                            boolean hasTarget = false;
                            for(Direction d : Direction.values()){
                                int i = 0;
                                while(true){
                                    i++;
                                    if(!contains(block.x+d.x*i, block.y+d.y*i, block.z+d.z*i))break;
                                    Block b = getBlock(block.x+d.x*i, block.y+d.y*i, block.z+d.z*i);
                                    if(b==null)continue;//air
                                    if(b.template.fuelVessel!=null){
                                        hasTarget = true;
                                        b.source = block;
                                        if(addDecals){
                                            decals.enqueue(new NeutronSourceTargetDecal(block.x, block.y, block.z, d));
                                            for(int j = 1; j<i; j++){
                                                decals.enqueue(new NeutronSourceLineDecal(block.x+d.x*j, block.y+d.y*j, block.z+d.z*j, d));
                                            }
                                            decals.enqueue(new NeutronSourceDecal(b.x, b.y, b.z, d.getOpposite()));
                                        }
                                    }
                                    if(b.template.blocksLOS())break;
                                }
                            }
                            if(!hasTarget){
                                decals.enqueue(new NeutronSourceNoTargetDecal(block.x, block.y, block.z));
                                return; 
                            }
                        }
                        block.casingValid = true;
                        if(addDecals)decals.enqueue(new BlockValidDecal(x, y, z));
                    }
                });
                calcCasing.finish();
                calcStep++;
                return true;
            case 1://open shields
                shieldsWere.clear();
                vesselGroupsWereActive.clear();
                vesselsWereActive.clear();
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.template.neutronShield!=null){
                        shieldsWere.put(block, block.isToggled());
                        block.setToggled(false);
                    }
                    openShields.progress = i/(double)allBlocks.size();
                }
                openShields.finish();
                calcStep++;
                return true;
            case 2://build groups
                vesselGroups.clear();
                for(Block b : allBlocks)b.vesselGroup = null;
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);//detect groups
                    VesselGroup group = getVesselGroup(block);
                    if(group==null)continue;//that's not a vessel group!
                    if(vesselGroups.contains(group))continue;//already know about that one!
                    vesselGroups.add(group);
                    buildGroups.progress = i/(double)allBlocks.size();
                }
                buildGroups.finish();
                calcStep++;
                return true;
            case 3://propogate neutron flux
                Queue<Decal> fluxDecals = new Queue<>();
                for(int i = 0; i<vesselGroups.size(); i++){
                    VesselGroup group = vesselGroups.get(i);
                    propogateNeutronFlux(group, false, fluxDecals);
                    propogateFlux.progress = i/(double)vesselGroups.size();
                }
                for(Block block : blocks){
                    if(block.template.fuelVessel!=null)fluxDecals.enqueue(new CellFluxDecal(block.x, block.y, block.z, block.vesselGroup==null?block.neutronFlux:block.vesselGroup.neutronFlux, block.vesselGroup==null?(block.fuel==null?0:block.fuel.stats.criticality):block.vesselGroup.criticality));
                }
                if(addDecals)for(Decal d : fluxDecals)decals.enqueue(d);
                propogateFlux.finish();
                calcStep++;
                return true;
            case 4://repropogate neutron flux
                calcSubstep++;
                rePropogateFlux.name = "Re-propogating Neutron Flux"+(calcSubstep>1?" ("+calcSubstep+")":"");
                int lastActive = 0;
                for(VesselGroup group : vesselGroups){
                    boolean wasActive = group.isActive();
                    group.hadFlux = group.neutronFlux;
                    HashMap<Block, Block> sources = new HashMap<>();
                    for(Block b : group.blocks)sources.put(b, b.source);
                    group.clearData();
                    for(Block b : sources.keySet())b.source = sources.get(b);
                    if(wasActive)lastActive+=group.size();
                    group.wasActive = wasActive;
                }
                fluxDecals = new Queue<>();
                for(int i = 0; i<blocks.size(); i++){
                    Block block = blocks.get(i);//why not vessel groups...?
                    rePropogateNeutronFlux(block, false, fluxDecals);
                    rePropogateFlux.progress = i/(double)blocks.size();
                }
                for(Block block : blocks){
                    if(block.template.fuelVessel!=null)fluxDecals.enqueue(new CellFluxDecal(block.x, block.y, block.z, block.vesselGroup==null?block.neutronFlux:block.vesselGroup.neutronFlux, block.vesselGroup==null?(block.fuel==null?0:block.fuel.stats.criticality):block.vesselGroup.criticality));
                }
                if(addDecals)for(Decal d : fluxDecals)decals.enqueue(d);
                int nowActive = 0;
                for(VesselGroup group : vesselGroups){
                    if(group.isActive())nowActive+=group.size();
                    if(!group.wasActive){
                        group.neutronFlux = group.hadFlux;
                    }
                }
                if(nowActive!=lastActive)return true;
                rePropogateFlux.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 5://post flux calc
                for(int i = 0; i<blocks.size(); i++){
                    Block block = blocks.get(i);
                    if(block.isFuelVessel())postFluxCalc(block, addDecals);
                    postFluxCalc.progress = i/(double)blocks.size();
                }
                postFluxCalc.finish();
                calcStep++;
                return true;
            case 6://heaters
                calcSubstep++;
                boolean somethingChanged = false;
                calcHeaters.name = "Calculating Heaters"+(calcSubstep>1?" ("+calcSubstep+")":"");
                for(int i = 0; i<blocks.size(); i++){
                    if(calculateHeater(blocks.get(i), addDecals))somethingChanged = true;
                    calcHeaters.progress = i/(double)blocks.size();
                }
                if(somethingChanged)return true;
                calcHeaters.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 7://init vessels
                for(int i = 0; i<vesselGroups.size(); i++){
                    VesselGroup group = vesselGroups.get(i);
                    group.positionalEfficiency*=group.getBunchingFactor();
                    for(Block block : group.blocks){
                        if(block.fuel==null)continue;
                        float criticalityModifier = (float) (1/(1+MathUtil.exp(2*(group.neutronFlux-2*block.vesselGroup.criticality))));
                        block.efficiency = block.fuel.stats.efficiency*group.positionalEfficiency*(block.source==null?1:block.source.template.neutronSource.efficiency)*criticalityModifier;
                        if(addDecals)decals.enqueue(new BlockValidDecal(block.x, block.y, block.z));
                    }
                    initVessels.progress = i/(double)vesselGroups.size();
                }
                initVessels.finish();
                calcStep++;
                return true;
            case 8://build clusters
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
                calcStep++;
                return true;
            case 9://calculate clusters
                synchronized(clusters){
                    for(int i = 0; i<clusters.size(); i++){
                        Cluster cluster = clusters.get(i);
                        int fuelVessels = 0;
                        ArrayList<VesselGroup> alreadyProcessedGroups = new ArrayList<>();
                        for(int j = 0; j<cluster.blocks.size(); j++){
                            Block b = cluster.blocks.get(j);
                            if(b.isFuelVesselActive()){
                                if(alreadyProcessedGroups.contains(b.vesselGroup))continue;
                                alreadyProcessedGroups.add(b.vesselGroup);
                                fuelVessels+=b.vesselGroup.size();
                                cluster.efficiency+=b.efficiency;
                                cluster.totalHeat+=b.vesselGroup.moderatorLines*b.fuel.stats.heat*b.vesselGroup.getBunchingFactor();
                                cluster.heatMult+=b.vesselGroup.getHeatMult();
                            }
                            if(b.isHeaterActive()){
                                cluster.totalCooling+=b.heaterRecipe==null?0:b.heaterRecipe.stats.cooling;
                            }
                            if(b.isShieldActive()){
                                cluster.totalHeat+=b.template.neutronShield.heatPerFlux*b.neutronFlux;
                            }
                            if(b.isIrradiatorActive()){
                                cluster.irradiation+=b.neutronFlux;
                                cluster.totalHeat+=b.irradiatorRecipe.stats.heat*b.neutronFlux;
                            }
                            calcClusters.progress = (i+j/(double)cluster.blocks.size())/(double)clusters.size();
                        }
                        cluster.efficiency/=fuelVessels;
                        cluster.heatMult/=fuelVessels;
                        if(Double.isNaN(cluster.efficiency))cluster.efficiency = 0;
                        if(Double.isNaN(cluster.heatMult))cluster.heatMult = 0;
                        cluster.netHeat = cluster.totalHeat-cluster.totalCooling;
                        if(cluster.totalCooling==0)cluster.coolingPenaltyMult = 1;
                        else cluster.coolingPenaltyMult = Math.min(1, (cluster.totalHeat+getSpecificConfiguration().settings.coolingEfficiencyLeniency)/(float)cluster.totalCooling);
                        cluster.efficiency*=cluster.coolingPenaltyMult;
                        totalFuelVessels+=fuelVessels;
                        totalCooling+=cluster.totalCooling;
                        totalHeat+=cluster.totalHeat;
                        netHeat+=cluster.netHeat;
                        totalEfficiency+=cluster.efficiency*fuelVessels;
                        totalHeatMult+=cluster.heatMult*fuelVessels;
                        totalIrradiation+=cluster.irradiation;
                        if(cluster.totalHeat==0)cluster.isConnectedToWall = true;
                        calcClusters.progress = (i+1)/(double)clusters.size();
                    }
                }
                calcClusters.finish();
                calcStep++;
                return true;
            case 10://calculate stats
                totalEfficiency/=totalFuelVessels;
                totalHeatMult/=totalFuelVessels;
                if(Double.isNaN(totalEfficiency))totalEfficiency = 0;
                if(Double.isNaN(totalHeatMult))totalHeatMult = 0;
                functionalBlocks = 0;
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.isFunctional())functionalBlocks++;
                    calcStats.progress = i/(double)allBlocks.size()/2;
                }
                int volume = getInternalVolume();
                sparsityMult = (float) (functionalBlocks/(float)volume>=getSpecificConfiguration().settings.sparsityPenaltyThreshold?1:getSpecificConfiguration().settings.sparsityPenaltyMultiplier+(1-getSpecificConfiguration().settings.sparsityPenaltyMultiplier)*Math.sin(Math.PI*functionalBlocks/(2*volume*getSpecificConfiguration().settings.sparsityPenaltyThreshold)));
                totalEfficiency*=sparsityMult;
                synchronized(clusters){
                    for(int i = 0; i<clusters.size(); i++){
                        Cluster c = clusters.get(i);
                        for(int j = 0; j<c.blocks.size(); j++){
                            Block b = c.blocks.get(j);
                            if(b.template.heater!=null&&b.heaterRecipe!=null){
                                float out = c.efficiency*sparsityMult;//TODO output rate?
                                boolean found = false;
                                for(FluidStack s : totalOutput){
                                    if(s.name.equals(b.heaterRecipe.getName())){//TODO output name
                                        s.amount+=out;
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found)totalOutput.add(new FluidStack(b.heaterRecipe.getName(), b.heaterRecipe.getDisplayName(), out));
                                totalTotalOutput+=out;
                            }
                            calcStats.progress = 0.5+(i/(double)clusters.size()+j/(double)c.blocks.size()/clusters.size())/2;
                        }
                        calcStats.progress = 0.5+i/(double)clusters.size()/2;
                    }
                }
                calcStats.finish();
                calcStep++;
                return true;
//</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="Shutdown Factor">
            case 11://clear data & calculate casing
                vesselsWereActive.clear();
                vesselGroupsWereActive.clear();
                for(Block b : getBlocks())if(b!=null&&b.isFuelVesselActive())vesselsWereActive.add(b);
                for(VesselGroup group : vesselGroups)if(group.isActive())vesselGroupsWereActive.add(group);
                clearData(allBlocks);
                validate();
                numControllers = missingCasings = 0;
                missingInputPorts.clear();
                missingOutputPorts.clear();
                forEachCasingPosition((x, y, z) -> {
                    Block block = getBlock(x, y, z);
                    if(block==null||!block.isCasing()){
                        missingCasings++;
                        if(addDecals)decals.enqueue(new MissingCasingDecal(x, y, z));
                    }
                    if(block!=null&&block.isCasing()){
                        if(block.isController()){
                            numControllers++;
                        }
                        if(block.template.neutronSource!=null){
                            boolean hasTarget = false;
                            for(Direction d : Direction.values()){
                                int i = 0;
                                while(true){
                                    i++;
                                    if(!contains(block.x+d.x*i, block.y+d.y*i, block.z+d.z*i))break;
                                    Block b = getBlock(block.x+d.x*i, block.y+d.y*i, block.z+d.z*i);
                                    if(b==null)continue;//air
                                    if(b.template.fuelVessel!=null){
                                        hasTarget = true;
                                        b.source = block;
                                        if(addDecals){
                                            decals.enqueue(new NeutronSourceTargetDecal(block.x, block.y, block.z, d));
                                            for(int j = 1; j<i; j++){
                                                decals.enqueue(new NeutronSourceLineDecal(block.x+d.x*j, block.y+d.y*j, block.z+d.z*j, d));
                                            }
                                            decals.enqueue(new NeutronSourceDecal(b.x, b.y, b.z, d.getOpposite()));
                                        }
                                    }
                                    if(b.template.blocksLOS())break;
                                }
                            }
                            if(!hasTarget){
                                decals.enqueue(new NeutronSourceNoTargetDecal(block.x, block.y, block.z));
                                return; 
                            }
                        }
                        block.casingValid = true;
                        if(addDecals)decals.enqueue(new BlockValidDecal(x, y, z));
                    }
                });
                shutdownCalcCasing.finish();
                calcStep++;
                return true;
            case 12://close shields
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.template.neutronShield!=null)block.setToggled(true);;
                    shutdownCloseShields.progress = i/(double)allBlocks.size();
                }
                shutdownCloseShields.finish();
                calcStep++;
                return true;
            case 13://build groups
                vesselGroups.clear();
                for(Block b : allBlocks)b.vesselGroup = null;
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);//detect groups
                    VesselGroup group = getVesselGroup(block);
                    if(group==null)continue;//that's not a vessel group!
                    if(vesselGroups.contains(group))continue;//already know about that one!
                    vesselGroups.add(group);
                    shutdownBuildGroups.progress = i/(double)allBlocks.size();
                }
                shutdownBuildGroups.finish();
                calcStep++;
                return true;
            case 14://propogate neutron flux
                fluxDecals = new Queue<>();
                for(int i = 0; i<vesselGroups.size(); i++){
                    VesselGroup group = vesselGroups.get(i);
                    propogateNeutronFlux(group, vesselGroupsWereActive.contains(group), fluxDecals);
                    shutdownPropogateFlux.progress = i/(double)vesselGroups.size();
                }
                for(Block block : allBlocks){
                    if(block.template.fuelVessel!=null)fluxDecals.enqueue(new CellFluxDecal(block.x, block.y, block.z, block.vesselGroup==null?block.neutronFlux:block.vesselGroup.neutronFlux, block.vesselGroup==null?(block.fuel==null?0:block.fuel.stats.criticality):block.vesselGroup.criticality));
                }
                if(addDecals)for(Decal d : fluxDecals)decals.enqueue(d);
                shutdownPropogateFlux.finish();
                calcStep++;
                return true;
            case 15://repropogate neutron flux
                calcSubstep++;
                shutdownRePropogateFlux.name = "Re-propogating Neutron Flux"+(calcSubstep>1?" ("+calcSubstep+")":"");
                lastActive = 0;
                for(VesselGroup group : vesselGroups){
                    boolean wasActive = group.isActive();
                    group.hadFlux = group.neutronFlux;
                    HashMap<Block, Block> sources = new HashMap<>();
                    for(Block b : group.blocks)sources.put(b, b.source);
                    group.clearData();
                    for(Block b : sources.keySet())b.source = sources.get(b);
                    if(wasActive)lastActive+=group.size();
                    group.wasActive = wasActive;
                }
                fluxDecals = new Queue<>();
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);//why not vessel groups...?
                    rePropogateNeutronFlux(block, vesselsWereActive.contains(block), fluxDecals);
                    shutdownRePropogateFlux.progress = i/(double)blocks.size();
                }
                for(Block block : allBlocks){
                    if(block.template.fuelVessel!=null)fluxDecals.enqueue(new CellFluxDecal(block.x, block.y, block.z, block.vesselGroup==null?block.neutronFlux:block.vesselGroup.neutronFlux, block.vesselGroup==null?(block.fuel==null?0:block.fuel.stats.criticality):block.vesselGroup.criticality));
                }
                for(Decal d : fluxDecals)shutdownFluxDecals.enqueue(d);
                if(addDecals)for(Decal d : fluxDecals)decals.enqueue(d);
                nowActive = 0;
                for(VesselGroup group : vesselGroups){
                    if(group.isActive())nowActive+=group.size();
                    if(!group.wasActive){
                        group.neutronFlux = group.hadFlux;
                    }
                }
                if(nowActive!=lastActive)return true;
                shutdownRePropogateFlux.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 16://post flux calc
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.isFuelVessel())postFluxCalc(block, addDecals);
                    shutdownPostFluxCalc.progress = i/(double)allBlocks.size();
                }
                shutdownPostFluxCalc.finish();
                calcStep++;
                return true;
            case 17://heaters
                calcSubstep++;
                somethingChanged = false;
                shutdownCalcHeaters.name = "Calculating Heaters"+(calcSubstep>1?" ("+calcSubstep+")":"");
                for(int i = 0; i<allBlocks.size(); i++){
                    if(calculateHeater(allBlocks.get(i), addDecals))somethingChanged = true;
                    shutdownCalcHeaters.progress = i/(double)allBlocks.size();
                }
                if(somethingChanged)return true;
                shutdownCalcHeaters.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 18://init vessels
                for(int i = 0; i<vesselGroups.size(); i++){
                    VesselGroup group = vesselGroups.get(i);
                    group.positionalEfficiency*=group.getBunchingFactor();
                    for(Block block : group.blocks){
                        if(block.fuel==null)continue;
                        float criticalityModifier = (float) (1/(1+MathUtil.exp(2*(group.neutronFlux-2*block.vesselGroup.criticality))));
                        block.efficiency = block.fuel.stats.efficiency*group.positionalEfficiency*(block.source==null?1:block.source.template.neutronSource.efficiency)*criticalityModifier;
                        if(addDecals)decals.enqueue(new BlockValidDecal(block.x, block.y, block.z));
                    }
                    shutdownInitVessels.progress = i/(double)vesselGroups.size();
                }
                shutdownInitVessels.finish();
                calcStep++;
                return true;
            case 19://build clusters
                for(int i = 0; i<allBlocks.size(); i++){//detect clusters
                    Cluster cluster = getCluster(allBlocks.get(i));
                    if(cluster==null)continue;//that's not a cluster!
                    synchronized(clusters){
                        if(clusters.contains(cluster))continue;//already know about that one!
                        clusters.add(cluster);
                    }
                    shutdownBuildClusters.progress = i/(double)allBlocks.size();
                }
                shutdownBuildClusters.finish();
                calcStep++;
                return true;
            case 20://calculate clusters
                synchronized(clusters){
                    for(int i = 0; i<clusters.size(); i++){
                        Cluster cluster = clusters.get(i);
                        int fuelVessels = 0;
                        ArrayList<VesselGroup> alreadyProcessedGroups = new ArrayList<>();
                        for(int j = 0; j<cluster.blocks.size(); j++){
                            Block b = cluster.blocks.get(j);
                            if(b.isFuelVesselActive()){
                                if(alreadyProcessedGroups.contains(b.vesselGroup))continue;
                                alreadyProcessedGroups.add(b.vesselGroup);
                                fuelVessels+=b.vesselGroup.size();
                                cluster.efficiency+=b.efficiency;
                                cluster.totalHeat+=b.vesselGroup.moderatorLines*b.fuel.stats.heat*b.vesselGroup.getBunchingFactor();
                                cluster.heatMult+=b.vesselGroup.getHeatMult();
                            }
                            if(b.isHeaterActive()){
                                cluster.totalCooling+=b.heaterRecipe==null?0:b.heaterRecipe.stats.cooling;
                            }
                            if(b.isShieldActive()){
                                cluster.totalHeat+=b.template.neutronShield.heatPerFlux*b.neutronFlux;
                            }
                            if(b.isIrradiatorActive()){
                                cluster.irradiation+=b.neutronFlux;
                                cluster.totalHeat+=b.irradiatorRecipe.stats.heat*b.neutronFlux;
                            }
                            calcClusters.progress = (i+j/(double)cluster.blocks.size())/(double)clusters.size();
                        }
                        cluster.efficiency/=fuelVessels;
                        cluster.heatMult/=fuelVessels;
                        if(Double.isNaN(cluster.efficiency))cluster.efficiency = 0;
                        if(Double.isNaN(cluster.heatMult))cluster.heatMult = 0;
                        cluster.netHeat = cluster.totalHeat-cluster.totalCooling;
                        if(cluster.totalCooling==0)cluster.coolingPenaltyMult = 1;
                        else cluster.coolingPenaltyMult = Math.min(1, (cluster.totalHeat+getSpecificConfiguration().settings.coolingEfficiencyLeniency)/(float)cluster.totalCooling);
                        cluster.efficiency*=cluster.coolingPenaltyMult;
                        totalFuelVessels+=fuelVessels;
                        totalCooling+=cluster.totalCooling;
                        totalHeat+=cluster.totalHeat;
                        netHeat+=cluster.netHeat;
                        totalEfficiency+=cluster.efficiency*fuelVessels;
                        totalHeatMult+=cluster.heatMult*fuelVessels;
                        totalIrradiation+=cluster.irradiation;
                        if(cluster.totalHeat==0)cluster.isConnectedToWall = true;
                        shutdownCalcClusters.progress = (i+1)/(double)clusters.size();
                    }
                }
                shutdownCalcClusters.finish();
                calcStep++;
                return true;
            case 21://calculate stats
                totalEfficiency/=totalFuelVessels;
                totalHeatMult/=totalFuelVessels;
                if(Double.isNaN(totalEfficiency))totalEfficiency = 0;
                if(Double.isNaN(totalHeatMult))totalHeatMult = 0;
                functionalBlocks = 0;
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.isFunctional())functionalBlocks++;
                    shutdownCalcStats.progress = i/(double)allBlocks.size()/2;
                }
                volume = getInternalVolume();
                sparsityMult = (float) (functionalBlocks/(float)volume>=getSpecificConfiguration().settings.sparsityPenaltyThreshold?1:getSpecificConfiguration().settings.sparsityPenaltyMultiplier+(1-getSpecificConfiguration().settings.sparsityPenaltyMultiplier)*Math.sin(Math.PI*functionalBlocks/(2*volume*getSpecificConfiguration().settings.sparsityPenaltyThreshold)));
                totalEfficiency*=sparsityMult;
                synchronized(clusters){
                    for(int i = 0; i<clusters.size(); i++){
                        Cluster c = clusters.get(i);
                        for(int j = 0; j<c.blocks.size(); j++){
                            Block b = c.blocks.get(j);
                            if(b.template.heater!=null&&b.heaterRecipe!=null){
                                float out = c.efficiency*sparsityMult;//TODO output rate?
                                boolean found = false;
                                for(FluidStack s : totalOutput){
                                    if(s.name.equals(b.heaterRecipe.getName())){//TODO output name
                                        s.amount+=out;
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found)totalOutput.add(new FluidStack(b.heaterRecipe.getName(), b.heaterRecipe.getDisplayName(), out));
                                totalTotalOutput+=out;
                            }
                            shutdownCalcStats.progress = 0.5+(i/(double)clusters.size()+j/(double)c.blocks.size()/clusters.size())/2;
                        }
                        shutdownCalcStats.progress = 0.5+i/(double)clusters.size()/2;
                    }
                }
                shutdownCalcStats.finish();
                calcShutdown.finish();
                offOutput = totalTotalOutput;
                calcStep++;
                return true;
//</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="Partial Shutdown">
            case 22://clear data & calculate casing
                clearData(allBlocks);
                validate();
                numControllers = missingCasings = 0;
                missingInputPorts.clear();
                missingOutputPorts.clear();
                forEachCasingPosition((x, y, z) -> {
                    Block block = getBlock(x, y, z);
                    if(block==null||!block.isCasing()){
                        missingCasings++;
                        if(addDecals)decals.enqueue(new MissingCasingDecal(x, y, z));
                    }
                    if(block!=null&&block.isCasing()){
                        if(block.isController()){
                            numControllers++;
                        }
                        if(block.template.neutronSource!=null){
                            boolean hasTarget = false;
                            for(Direction d : Direction.values()){
                                int i = 0;
                                while(true){
                                    i++;
                                    if(!contains(block.x+d.x*i, block.y+d.y*i, block.z+d.z*i))break;
                                    Block b = getBlock(block.x+d.x*i, block.y+d.y*i, block.z+d.z*i);
                                    if(b==null)continue;//air
                                    if(b.isFuelVessel()){
                                        hasTarget = true;
                                        b.source = block;
                                        if(addDecals){
                                            decals.enqueue(new NeutronSourceTargetDecal(block.x, block.y, block.z, d));
                                            for(int j = 1; j<i; j++){
                                                decals.enqueue(new NeutronSourceLineDecal(block.x+d.x*j, block.y+d.y*j, block.z+d.z*j, d));
                                            }
                                            decals.enqueue(new NeutronSourceDecal(b.x, b.y, b.z, d.getOpposite()));
                                        }
                                    }
                                    if(b.template.blocksLOS())break;
                                }
                            }
                            if(!hasTarget){
                                decals.enqueue(new NeutronSourceNoTargetDecal(block.x, block.y, block.z));
                                return; 
                            }
                        }
                        block.casingValid = true;
                        if(addDecals)decals.enqueue(new BlockValidDecal(x, y, z));
                    }
                });
                partialShutdownCalcCasing.finish();
                calcStep++;
                return true;
            case 23://reset shields
                for(Block b : shieldsWere.keySet()){
                    b.setToggled(shieldsWere.get(b));
                }
                partialShutdownResetShields.finish();
                calcStep++;
                return true;
            case 24://build groups
                vesselGroups.clear();
                for(Block b : allBlocks)b.vesselGroup = null;
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);//detect groups
                    VesselGroup group = getVesselGroup(block);
                    if(group==null)continue;//that's not a vessel group!
                    if(vesselGroups.contains(group))continue;//already know about that one!
                    vesselGroups.add(group);
                    partialShutdownBuildGroups.progress = i/(double)allBlocks.size();
                }
                partialShutdownBuildGroups.finish();
                calcStep++;
                return true;
            case 25://propogate neutron flux
                fluxDecals = new Queue<>();
                for(int i = 0; i<vesselGroups.size(); i++){
                    VesselGroup group = vesselGroups.get(i);
                    propogateNeutronFlux(group, vesselGroupsWereActive.contains(group), fluxDecals);
                    partialShutdownPropogateFlux.progress = i/(double)vesselGroups.size();
                }
                for(Block block : allBlocks){
                    if(block.template.fuelVessel!=null)fluxDecals.enqueue(new CellFluxDecal(block.x, block.y, block.z, block.vesselGroup==null?block.neutronFlux:block.vesselGroup.neutronFlux, block.vesselGroup==null?(block.fuel==null?0:block.fuel.stats.criticality):block.vesselGroup.criticality));
                }
                for(Decal d : fluxDecals)initialFluxDecals.enqueue(d);
                if(addDecals)for(Decal d : fluxDecals)decals.enqueue(d);
                partialShutdownPropogateFlux.finish();
                calcStep++;
                return true;
            case 26://repropogate neutron flux
                calcSubstep++;
                partialShutdownRePropogateFlux.name = "Re-propogating Neutron Flux"+(calcSubstep>1?" ("+calcSubstep+")":"");
                lastActive = 0;
                for(VesselGroup group : vesselGroups){
                    boolean wasActive = group.isActive();
                    group.hadFlux = group.neutronFlux;
                    HashMap<Block, Block> sources = new HashMap<>();
                    for(Block b : group.blocks)sources.put(b, b.source);
                    group.clearData();
                    for(Block b : sources.keySet())b.source = sources.get(b);
                    if(wasActive)lastActive+=group.size();
                    group.wasActive = wasActive;
                }
                fluxDecals = new Queue<>();
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);//why not vessel groups...?
                    rePropogateNeutronFlux(block, vesselsWereActive.contains(block), fluxDecals);
                    partialShutdownRePropogateFlux.progress = i/(double)allBlocks.size();
                }
                for(Block block : allBlocks){
                    if(block.template.fuelVessel!=null)fluxDecals.enqueue(new CellFluxDecal(block.x, block.y, block.z, block.vesselGroup==null?block.neutronFlux:block.vesselGroup.neutronFlux, block.vesselGroup==null?(block.fuel==null?0:block.fuel.stats.criticality):block.vesselGroup.criticality));
                }
                for(Decal d : fluxDecals)finalFluxDecals.enqueue(d);
                if(addDecals)for(Decal d : fluxDecals)decals.enqueue(d);
                nowActive = 0;
                for(VesselGroup group : vesselGroups){
                    if(group.isActive())nowActive+=group.size();
                    if(!group.wasActive){
                        group.neutronFlux = group.hadFlux;
                    }
                }
                if(nowActive!=lastActive)return true;
                partialShutdownRePropogateFlux.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 27://post flux calc
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.isFuelVessel())postFluxCalc(block, addDecals);
                    partialShutdownPostFluxCalc.progress = i/(double)allBlocks.size();
                }
                partialShutdownPostFluxCalc.finish();
                calcStep++;
                return true;
            case 28://heaters
                calcSubstep++;
                somethingChanged = false;
                partialShutdownCalcHeaters.name = "Calculating Heaters"+(calcSubstep>1?" ("+calcSubstep+")":"");
                for(int i = 0; i<allBlocks.size(); i++){
                    if(calculateHeater(allBlocks.get(i), addDecals))somethingChanged = true;
                    partialShutdownCalcHeaters.progress = i/(double)allBlocks.size();
                }
                if(somethingChanged)return true;
                partialShutdownCalcHeaters.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 29://init vessels
                for(int i = 0; i<vesselGroups.size(); i++){
                    VesselGroup group = vesselGroups.get(i);
                    group.positionalEfficiency*=group.getBunchingFactor();
                    for(Block block : group.blocks){
                        if(block.fuel==null)continue;
                        float criticalityModifier = (float) (1/(1+MathUtil.exp(2*(group.neutronFlux-2*block.vesselGroup.criticality))));
                        block.efficiency = block.fuel.stats.efficiency*group.positionalEfficiency*(block.source==null?1:block.source.template.neutronSource.efficiency)*criticalityModifier;
                        if(addDecals)decals.enqueue(new BlockValidDecal(block.x, block.y, block.z));
                    }
                    partialShutdownInitVessels.progress = i/(double)vesselGroups.size();
                }
                partialShutdownInitVessels.finish();
                calcStep++;
                return true;
            case 30://build clusters
                for(int i = 0; i<allBlocks.size(); i++){//detect clusters
                    Cluster cluster = getCluster(allBlocks.get(i));
                    if(cluster==null)continue;//that's not a cluster!
                    synchronized(clusters){
                        if(clusters.contains(cluster))continue;//already know about that one!
                        clusters.add(cluster);
                    }
                    partialShutdownBuildClusters.progress = i/(double)allBlocks.size();
                }
                partialShutdownBuildClusters.finish();
                calcStep++;
                return true;
            case 31://calculate clusters
                synchronized(clusters){
                    for(int i = 0; i<clusters.size(); i++){
                        Cluster cluster = clusters.get(i);
                        int fuelVessels = 0;
                        ArrayList<VesselGroup> alreadyProcessedGroups = new ArrayList<>();
                        for(int j = 0; j<cluster.blocks.size(); j++){
                            Block b = cluster.blocks.get(j);
                            if(b.isFuelVesselActive()){
                                if(alreadyProcessedGroups.contains(b.vesselGroup))continue;
                                alreadyProcessedGroups.add(b.vesselGroup);
                                fuelVessels+=b.vesselGroup.size();
                                cluster.efficiency+=b.efficiency;
                                cluster.totalHeat+=b.vesselGroup.moderatorLines*b.fuel.stats.heat*b.vesselGroup.getBunchingFactor();
                                cluster.heatMult+=b.vesselGroup.getHeatMult();
                            }
                            if(b.isHeaterActive()){
                                cluster.totalCooling+=b.heaterRecipe==null?0:b.heaterRecipe.stats.cooling;
                            }
                            if(b.isShieldActive()){
                                cluster.totalHeat+=b.template.neutronShield.heatPerFlux*b.neutronFlux;
                            }
                            if(b.isIrradiatorActive()){
                                cluster.irradiation+=b.neutronFlux;
                                cluster.totalHeat+=b.irradiatorRecipe.stats.heat*b.neutronFlux;
                            }
                            calcClusters.progress = (i+j/(double)cluster.blocks.size())/(double)clusters.size();
                        }
                        cluster.efficiency/=fuelVessels;
                        cluster.heatMult/=fuelVessels;
                        if(Double.isNaN(cluster.efficiency))cluster.efficiency = 0;
                        if(Double.isNaN(cluster.heatMult))cluster.heatMult = 0;
                        cluster.netHeat = cluster.totalHeat-cluster.totalCooling;
                        if(cluster.totalCooling==0)cluster.coolingPenaltyMult = 1;
                        else cluster.coolingPenaltyMult = Math.min(1, (cluster.totalHeat+getSpecificConfiguration().settings.coolingEfficiencyLeniency)/(float)cluster.totalCooling);
                        cluster.efficiency*=cluster.coolingPenaltyMult;
                        totalFuelVessels+=fuelVessels;
                        totalCooling+=cluster.totalCooling;
                        totalHeat+=cluster.totalHeat;
                        netHeat+=cluster.netHeat;
                        totalEfficiency+=cluster.efficiency*fuelVessels;
                        totalHeatMult+=cluster.heatMult*fuelVessels;
                        totalIrradiation+=cluster.irradiation;
                        if(cluster.totalHeat==0)cluster.isConnectedToWall = true;
                        partialShutdownCalcClusters.progress = (i+1)/(double)clusters.size();
                    }
                }
                partialShutdownCalcClusters.finish();
                calcStep++;
                return true;
            case 32://calculate stats
                totalEfficiency/=totalFuelVessels;
                totalHeatMult/=totalFuelVessels;
                if(Double.isNaN(totalEfficiency))totalEfficiency = 0;
                if(Double.isNaN(totalHeatMult))totalHeatMult = 0;
                functionalBlocks = 0;
                ArrayList<NCPFElement> inputPortRecipes = new ArrayList<>();
                ArrayList<NCPFElement> outputPortRecipes = new ArrayList<>();
                BLOCK:for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.template.port==null)continue;
                    if(block.getRecipe()!=null){
                        if(block.template.port.output){
                            if(!outputPortRecipes.contains(block.getRecipe()))outputPortRecipes.add(block.getRecipe());
                        }else{
                            if(!inputPortRecipes.contains(block.getRecipe()))inputPortRecipes.add(block.getRecipe());
                        }
                    }
                    partialShutdownCalcStats.progress = i/(double)allBlocks.size()/4;
                }
                BLOCK:for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.isFunctional())functionalBlocks++;
                    if(block.template.parent!=null)continue;
                    if(block.getRecipe()!=null){
                        if(!inputPortRecipes.contains(block.getRecipe()))missingInputPorts.put(block.getRecipe(), block.template);
                        if(!outputPortRecipes.contains(block.getRecipe()))missingOutputPorts.put(block.getRecipe(), block.template);
                    }
                    partialShutdownCalcStats.progress = 0.25+i/(double)allBlocks.size()/4;
                }
                volume = getInternalVolume();
                sparsityMult = (float) (functionalBlocks/(float)volume>=getSpecificConfiguration().settings.sparsityPenaltyThreshold?1:getSpecificConfiguration().settings.sparsityPenaltyMultiplier+(1-getSpecificConfiguration().settings.sparsityPenaltyMultiplier)*Math.sin(Math.PI*functionalBlocks/(2*volume*getSpecificConfiguration().settings.sparsityPenaltyThreshold)));
                totalEfficiency*=sparsityMult;
                synchronized(clusters){
                    for(int i = 0; i<clusters.size(); i++){
                        Cluster c = clusters.get(i);
                        for(int j = 0; j<c.blocks.size(); j++){
                            Block b = c.blocks.get(j);
                            if(b.template.heater!=null&&b.heaterRecipe!=null){
                                float out = c.efficiency*sparsityMult;//TODO output rate?
                                boolean found = false;
                                for(FluidStack s : totalOutput){
                                    if(s.name.equals(b.heaterRecipe.getName())){//TODO output name
                                        s.amount+=out;
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found)totalOutput.add(new FluidStack(b.heaterRecipe.getName(), b.heaterRecipe.getDisplayName(), out));
                                totalTotalOutput+=out;
                            }
                            calcStats.progress = 0.5+(i/(double)clusters.size()+j/(double)c.blocks.size()/clusters.size())/2;
                        }
                        calcStats.progress = 0.5+i/(double)clusters.size()/2;
                    }
                }
                partialShutdownCalcStats.finish();
                calcPartialShutdown.finish();
                shutdownFactor = 1-(offOutput/totalTotalOutput);
                calcStep = 0;
                return false;
//</editor-fold>
            default:
                throw new IllegalStateException("Invalid calculation step: "+calcStep+"!");
        }
    }
    public void propogateNeutronFlux(VesselGroup group, boolean force, Queue<Decal> fluxDecals){
        for(Block b : group.blocks){
            propogateNeutronFlux(b, force, fluxDecals);
        }
    }
    public void rePropogateNeutronFlux(VesselGroup group, boolean force, Queue<Decal> fluxDecals){
        for(Block b : group.blocks){
            rePropogateNeutronFlux(b, force, fluxDecals);
        }
    }
    public void propogateNeutronFlux(Block that, boolean force, Queue<Decal> fluxDecals){
        if(!that.isFuelVessel())return;
        if(that.fuel==null)return;
        if(!force&&!that.vesselGroup.isPrimed()&&that.vesselGroup.neutronFlux<that.vesselGroup.criticality)return;
        if(that.hasPropogated)return;
        that.hasPropogated = true;
        for(Direction d : Direction.values()){
            int flux = 0;
            int length = 0;
            float efficiency = 0;
            for(int i = 1; i<=getSpecificConfiguration().settings.neutronReach+1; i++){
                if(!contains(that.x+d.x*i, that.y+d.y*i, that.z+d.z*i))break;
                Block block = getBlock(that.x+d.x*i, that.y+d.y*i, that.z+d.z*i);
                if(block==null)break;
                if(block.isModerator()){
                    flux+=block.template.moderator.flux;
                    efficiency+=block.template.moderator.efficiency;
                    length++;
                    continue;
                }
                if(block.isShield()){
                    efficiency+=block.template.neutronShield.efficiency;
                    length++;
                    continue;
                }
                if(block.isFuelVessel()){
                    if(length==0)break;
                    if(block.fuel==null)break;
                    block.vesselGroup.neutronFlux+=flux;
                    block.vesselGroup.moderatorLines++;
                    if(flux>0)block.vesselGroup.positionalEfficiency+=efficiency/length;
                    int f = 0;
                    for(int j = 1; j<i; j++){
                        f+=getBlock(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j).template.moderator.flux;
                        fluxDecals.enqueue(new OverhaulModeratorLineDecal(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j, d, f, efficiency/length));
                    }
                    fluxDecals.enqueue(new AdjacentModeratorLineDecal(that.x, that.y, that.z, d, efficiency/length));
                    propogateNeutronFlux(block.vesselGroup, false, fluxDecals);
                    break;
                }
                if(block.isReflector()){
                    if(length==0)break;
                    if(length>getSpecificConfiguration().settings.neutronReach/2)break;
                    that.vesselGroup.neutronFlux+=flux*2*block.template.reflector.reflectivity;
                    if(flux>0)that.vesselGroup.positionalEfficiency+=efficiency/length*block.template.reflector.efficiency;
                    that.vesselGroup.moderatorLines++;
                    int f = 0;
                    for(int j = 1; j<i; j++){
                        f+=getBlock(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j).template.moderator.flux;
                        fluxDecals.enqueue(new OverhaulModeratorLineDecal(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j, d, f, efficiency/length));
                    }
                    f = 0;
                    for(int j = i-1; j>=1; j--){
                        f+=getBlock(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j).template.moderator.flux*block.template.reflector.reflectivity;
                        fluxDecals.enqueue(new OverhaulModeratorLineDecal(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j, d.getOpposite(), (int)(flux*block.template.reflector.reflectivity)+f, efficiency/length));
                    }
                    fluxDecals.enqueue(new AdjacentModeratorLineDecal(that.x, that.y, that.z, d, efficiency/length));
                    fluxDecals.enqueue(new ReflectorAdjacentModeratorLineDecal(block.x, block.y, block.z, d.getOpposite()));
                    break;
                }
                if(block.isIrradiator()){
                    if(length==0)break;
                    if(block.irradiatorRecipe==null)break;
                    that.vesselGroup.moderatorLines++;
                    if(flux>0)that.vesselGroup.positionalEfficiency+=efficiency/length*block.irradiatorRecipe.stats.efficiency;
                    int f = 0;
                    for(int j = 1; j<i; j++){
                        f+=getBlock(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j).template.moderator.flux;
                        fluxDecals.enqueue(new OverhaulModeratorLineDecal(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j, d, f, efficiency/length));
                    }
                    fluxDecals.enqueue(new AdjacentModeratorLineDecal(that.x, that.y, that.z, d, efficiency/length));
                    fluxDecals.enqueue(new IrradiatorAdjacentModeratorLineDecal(block.x, block.y, block.z, d.getOpposite()));
                    break;
                }
                break;
            }
        }
    }
    public void rePropogateNeutronFlux(Block that, boolean force, Queue<Decal> fluxDecals){
        if(!that.isFuelVessel())return;
        if(that.fuel==null)return;
        if(!that.vesselGroup.wasActive)return;
        if(!force&&!that.vesselGroup.isPrimed()&&that.vesselGroup.neutronFlux<that.vesselGroup.criticality)return;
        if(that.hasPropogated)return;
        that.hasPropogated = true;
        for(Direction d : Direction.values()){
            int flux = 0;
            int length = 0;
            float efficiency = 0;
            for(int i = 1; i<=getSpecificConfiguration().settings.neutronReach+1; i++){
                if(!contains(that.x+d.x*i, that.y+d.y*i, that.z+d.z*i))break;
                Block block = getBlock(that.x+d.x*i, that.y+d.y*i, that.z+d.z*i);
                if(block==null)break;
                if(block.isModerator()){
                    flux+=block.template.moderator.flux;
                    efficiency+=block.template.moderator.efficiency;
                    length++;
                    continue;
                }
                if(block.isShield()){
                    efficiency+=block.template.neutronShield.efficiency;
                    length++;
                }
                if(block.isFuelVessel()){
                    if(length==0)break;
                    if(block.fuel==null)break;
                    block.vesselGroup.neutronFlux+=flux;
                    block.vesselGroup.moderatorLines++;
                    if(flux>0)block.vesselGroup.positionalEfficiency+=efficiency/length;
                    int f = 0;
                    for(int j = 1; j<i; j++){
                        f+=getBlock(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j).template.moderator.flux;
                        fluxDecals.enqueue(new OverhaulModeratorLineDecal(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j, d, f, efficiency/length));
                    }
                    fluxDecals.enqueue(new AdjacentModeratorLineDecal(that.x, that.y, that.z, d, efficiency/length));
                    rePropogateNeutronFlux(block.vesselGroup, false, fluxDecals);
                    break;
                }
                if(block.isReflector()){
                    if(length==0)break;
                    if(length>getSpecificConfiguration().settings.neutronReach/2)break;
                    that.vesselGroup.neutronFlux+=flux*2*block.template.reflector.reflectivity;
                    if(flux>0)that.vesselGroup.positionalEfficiency+=efficiency/length*block.template.reflector.efficiency;
                    that.vesselGroup.moderatorLines++;
                    int f = 0;
                    for(int j = 1; j<i; j++){
                        f+=getBlock(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j).template.moderator.flux;
                        fluxDecals.enqueue(new OverhaulModeratorLineDecal(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j, d, f, efficiency/length));
                    }
                    f = 0;
                    for(int j = i-1; j>=1; j--){
                        f+=getBlock(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j).template.moderator.flux*block.template.reflector.reflectivity;
                        fluxDecals.enqueue(new OverhaulModeratorLineDecal(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j, d.getOpposite(), (int)(flux*block.template.reflector.reflectivity)+f, efficiency/length));
                    }
                    fluxDecals.enqueue(new AdjacentModeratorLineDecal(that.x, that.y, that.z, d, efficiency/length));
                    fluxDecals.enqueue(new ReflectorAdjacentModeratorLineDecal(block.x, block.y, block.z, d.getOpposite()));
                    break;
                }
                if(block.isIrradiator()){
                    if(length==0)break;
                    if(block.irradiatorRecipe==null)break;
                    that.vesselGroup.moderatorLines++;
                    if(flux>0)that.vesselGroup.positionalEfficiency+=efficiency/length*block.irradiatorRecipe.stats.efficiency;
                    int f = 0;
                    for(int j = 1; j<i; j++){
                        f+=getBlock(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j).template.moderator.flux;
                        fluxDecals.enqueue(new OverhaulModeratorLineDecal(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j, d, f, efficiency/length));
                    }
                    fluxDecals.enqueue(new AdjacentModeratorLineDecal(that.x, that.y, that.z, d, efficiency/length));
                    fluxDecals.enqueue(new IrradiatorAdjacentModeratorLineDecal(block.x, block.y, block.z, d.getOpposite()));
                    break;
                }
                break;
            }
        }
    }
    public void postFluxCalc(Block that, boolean addDecals){
        if(!that.isFuelVesselActive())return;
        if(that.fuel==null)return;
        for(Direction d : Direction.values()){
            int flux = 0;
            int length = 0;
            HashMap<Block, Integer> shieldFluxes = new HashMap<>();
            Queue<Block> toActivate = new Queue<>();
            Queue<Block> toValidate = new Queue<>();
            for(int i = 1; i<=getSpecificConfiguration().settings.neutronReach+1; i++){
                if(!contains(that.x+d.x*i, that.y+d.y*i, that.z+d.z*i))break;
                Block block = getBlock(that.x+d.x*i, that.y+d.y*i, that.z+d.z*i);
                if(block==null)break;
                boolean skip = false;
                if(block.isModerator()){
                    length++;
                    flux+=block.template.moderator.flux;
                    if(i==1)toActivate.enqueue(block);
                    toValidate.enqueue(block);
                    skip = true;
                }
                if(block.isShield()){
                    length++;
                    if(i==1)toActivate.enqueue(block);
                    toValidate.enqueue(block);
                    block.shieldActive = true;
                    if(addDecals)decals.enqueue(new BlockValidDecal(block.x, block.y, block.z));
                    shieldFluxes.put(block, flux);
                    skip = true;
                }
                if(skip)continue;
                if(block.isFuelVesselActive()){
                    if(length==0)break;
                    if(block.fuel==null)break;
                    for(Block b : shieldFluxes.keySet()){
                        b.neutronFlux+=shieldFluxes.get(b);
                    }
                    for(Block b : toActivate){
                        b.moderatorActive = true;
                        if(addDecals){
                            decals.enqueue(new ModeratorActiveDecal(b.x, b.y, b.z, d.getOpposite()));
                        }
                    }
                    for(Block b : toValidate){
                        b.moderatorValid = true;
                        if(addDecals)decals.enqueue(new BlockValidDecal(b.x, b.y, b.z));
                    }
                    break;
                }
                if(block.isReflector()){
                    if(length==0)break;
                    if(length>getSpecificConfiguration().settings.neutronReach/2)break;
                    block.reflectorActive = true;
                    if(addDecals)decals.enqueue(new BlockValidDecal(block.x, block.y, block.z));
                    for(Block b : shieldFluxes.keySet()){
                        b.neutronFlux+=flux*(1+block.template.reflector.reflectivity);
                    }
                    for(Block b : toActivate){
                        b.moderatorActive = true;
                        if(addDecals){
                            decals.enqueue(new ModeratorActiveDecal(b.x, b.y, b.z, d.getOpposite()));
                        }
                    }
                    for(Block b : toValidate){
                        b.moderatorValid = true;
                        if(addDecals)decals.enqueue(new BlockValidDecal(b.x, b.y, b.z));
                    }
                    break;
                }
                if(block.isIrradiator()){
                    if(length==0)break;
                    if(block.irradiatorRecipe==null)break;
                    for(Block b : shieldFluxes.keySet()){
                        b.neutronFlux+=shieldFluxes.get(b);
                    }
                    block.neutronFlux+=flux;
                    if(addDecals)decals.enqueue(new BlockValidDecal(block.x, block.y, block.z));
                    for(Block b : toActivate){
                        b.moderatorActive = true;
                        if(addDecals){
                            decals.enqueue(new ModeratorActiveDecal(b.x, b.y, b.z, d.getOpposite()));
                        }
                    }
                    for(Block b : toValidate){
                        b.moderatorValid = true;
                        if(addDecals)decals.enqueue(new BlockValidDecal(b.x, b.y, b.z));
                    }
                    break;
                }
                break;
            }
        }
        that.hasPropogated = true;
    }
    /**
     * Calculates the heater
     * @param block the block
     * @param addDecals whether or not to add decals
     * @return <code>true</code> if the heater state has changed
     */
    public boolean calculateHeater(Block block, boolean addDecals){
        if(!block.isHeater())return false;
        if(block.heaterRecipe==null)return false;
        boolean wasValid = block.heaterValid;
        for(NCPFPlacementRule rule : block.getRules()){
            if(!rule.isValid(block, this)){
                if(block.heaterValid&&addDecals)decals.enqueue(new BlockInvalidDecal(block.x, block.y, block.z));
                block.heaterValid = false;
                return wasValid!=block.heaterValid;
            }
        }
        if(!block.heaterValid&&addDecals)decals.enqueue(new BlockValidDecal(block.x, block.y, block.z));
        block.heaterValid = true;
        return wasValid!=block.heaterValid;
    }
    @Override
    public FormattedText getTooltip(boolean full){
        if(this.showDetails!=null)full = this.showDetails;
        String outs = "";
        ArrayList<FluidStack> outputList = new ArrayList<>(totalOutput);
        Collections.sort(outputList, (o1, o2) -> {
            return (int)(o2.amount-o1.amount);
        });
        for(FluidStack stack : outputList){
            if(full)outs+="\n "+Math.round(stack.amount)+" mb/t of whatever "+stack.getDisplayName()+" gets turned into";
        }
        synchronized(clusters){
            int validClusters = 0;
            for(Cluster c : clusters){
                if(c.isValid())validClusters++;
            }
            FormattedText text = new FormattedText();
            if(numControllers<1)text.addText("No controller!", Core.theme.getTooltipInvalidTextColor());
            if(numControllers>1)text.addText("Too many controllers!", Core.theme.getTooltipInvalidTextColor());
            if(missingCasings>0)text.addText("Casing incomplete! (Missing "+missingCasings+")", Core.theme.getTooltipInvalidTextColor());
            if(!missingInputPorts.isEmpty()){
                text.addText("Missing "+missingInputPorts.size()+" input port"+(missingInputPorts.size()==1?"":"s")+":", Core.theme.getTooltipInvalidTextColor());
                for(NCPFElement key : missingInputPorts.keySet()){
                    text.addText(" "+missingInputPorts.get(key).getDisplayName()+" - "+key.getDisplayName(), Core.theme.getTooltipInvalidTextColor());
                }
            }
            if(!missingOutputPorts.isEmpty()){
                text.addText("Missing "+missingOutputPorts.size()+" output port"+(missingOutputPorts.size()==1?"":"s")+":", Core.theme.getTooltipInvalidTextColor());
                for(NCPFElement key : missingOutputPorts.keySet()){
                    text.addText(" "+missingOutputPorts.get(key).getDisplayName()+" - "+key.getDisplayName(), Core.theme.getTooltipInvalidTextColor());
                }
            }
            text.addText("Total output: "+Math.round(totalTotalOutput)+" mb/t"+outs+"\n"
                    + "Total Heat: "+totalHeat+"H/t\n"
                    + "Total Cooling: "+totalCooling+"H/t\n"
                    + "Net Heat: "+netHeat+"H/t\n"
                    + "Overall Efficiency: "+MathUtil.percent(totalEfficiency, 0)+"\n"
                    + "Overall Heat Multiplier: "+MathUtil.percent(totalHeatMult, 0)+"\n"
                    + "Sparsity Penalty Multiplier: "+Math.round(sparsityMult*10000)/10000d+"\n"
                    + "Clusters: "+(validClusters==clusters.size()?clusters.size():(validClusters+"/"+clusters.size()))+"\n"
                    + "Total Irradiation: "+totalIrradiation+"\n"
                    + "Shutdown Factor: "+MathUtil.percent(shutdownFactor, 2), Core.theme.getTooltipTextColor());
            text.addText(getModuleTooltip()+"\n");
            for(BlockElement b : getSpecificConfiguration().blocks){
                if(b.port!=null)continue;
                String txt = "";
                for(Fuel f : b.fuels){
                    int i = getRecipeCount(f);
                    if(i>0){
                        txt+="\n"+f.getDisplayName()+": "+i;
                    }
                }
                for(IrradiatorRecipe r : b.irradiatorRecipes){
                    int i = getRecipeCount(r);
                    if(i>0){
                        txt+="\n"+r.getDisplayName()+": "+i;
                    }
                }
                if(!txt.isEmpty())text.addText(txt);
            }
            if(full){
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
                        colors.put(str, Core.theme.getClusterInvalidColor());
                    }else if(!c.isConnectedToWall){
                        colors.put(str, Core.theme.getClusterDisconnectedColor());
                    }else if(c.netHeat>0)colors.put(str, Core.theme.getClusterOverheatingColor());
                    else if(c.coolingPenaltyMult!=1)colors.put(str, Core.theme.getClusterOvercoolingColor());
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
    public boolean validate(){
        return false;
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
    public int getRecipeCount(NCPFElement recipe){
        int count = 0;
        for(Block block : getBlocks()){
            if(block.template.parent!=null)continue;
            if(block.getRecipe()==recipe)count++;
        }
        return count;
    }
    public HashMap<NCPFElement, Integer> getRecipeCounts(){
        HashMap<NCPFElement, Integer> counts = new HashMap<>();
        for(Block block : getBlocks()){
            if(block.template.parent!=null)continue;
            NCPFElement recipe = block.getRecipe();
            if(recipe==null)continue;
            if(counts.containsKey(recipe))counts.put(recipe, counts.get(recipe)+1);
            else counts.put(recipe, 1);
        }
        return counts;
    }
    public OverhaulSFR convertToSFR() throws MissingConfigurationEntryException{
        throw new UnsupportedOperationException("Not yet implemented");
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
                if(block.template.createsCluster())return true;
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
                if(block.x==1||block.y==1||block.z==1)return true;
                if(block.x==getInternalWidth()||block.y==getInternalHeight()||block.z==getInternalDepth())return true;
            }
            return false;
        }
        public String getTooltip(){
            if(!isCreated())return "Invalid cluster!";
            if(!isValid())return "Cluster is not connected to the casing!";
            return "Efficiency: "+MathUtil.percent(efficiency, 0)+"\n"
                + "Total Heating: "+totalHeat+"H/t\n"
                + "Total Cooling: "+totalCooling+"H/t\n"
                + "Net Heating: "+netHeat+"H/t\n"
                + "Heat Multiplier: "+MathUtil.percent(heatMult, 0)+"\n"
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
    }
    public class VesselGroup{
        public ArrayList<Block> blocks = new ArrayList<>();
        public int criticality = 0;
        public int neutronFlux = 0;
        public int moderatorLines;
        public float positionalEfficiency;
        public int hadFlux;
        public boolean wasActive;
        public int openFaces = -1; 
        public VesselGroup(Block block){
            blocks.addAll(toList(getBlocks(block)));
            int fuelCriticality = 0;
            for(Block b : blocks){
                b.vesselGroup = this;
                if(b.fuel==null)continue;
                fuelCriticality = b.fuel.stats.criticality;
            }
            criticality = fuelCriticality*getSurfaceFactor();
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
            int maxDistance = getInternalVolume();//the algorithm requires a max search distance. Rather than changing that, I'll just be lazy and give it a big enough number
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
                        if(!OverhaulMSR.this.contains(block.x+dx, block.y+dy, block.z+dz))continue;
                        Block newBlock = getBlock(block.x+dx,block.y+dy,block.z+dz);
                        if(newBlock==null)continue;
                        if(!newBlock.isEqual(start))continue;//not the same block
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
        public int size(){
            return blocks.size();
        }
        public int getOpenFaces(){
            if(openFaces==-1){
                int open = 0;
                for(Block b1 : blocks){
                    DIRECTION:for(Direction d : Direction.values()){
                        int x = b1.x+d.x;
                        int y = b1.y+d.y;
                        int z = b1.z+d.z;
                        for(Block b2 : blocks){
                            if(b2.x==x&&b2.y==y&&b2.z==z)continue DIRECTION;
                        }
                        open++;
                    }
                }
                openFaces = open;
            }
            return openFaces;
        }
        public int getBunchingFactor(){
            return 6*size()/getOpenFaces();
        }
        public int getSurfaceFactor(){
            return getOpenFaces()/6;
        }
        private boolean isActive(){
            return neutronFlux>=criticality;
        }
        private void clearData(){
            for(Block b : blocks)b.clearData();
            openFaces = -1;
            wasActive = false;
            neutronFlux = 0;
            positionalEfficiency = 0;
            moderatorLines = 0;
        }
        public float getHeatMult(){
            return moderatorLines*getBunchingFactor();
        }
        public int getRequiredSources(){
            return getSurfaceFactor();
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
    }
    @Override
    public synchronized void clearData(List<Block> blocks){
        super.clearData(blocks);
        synchronized(clusters){
            clusters.clear();
        }
        totalOutput.clear();
        shutdownFactor = totalTotalOutput = totalEfficiency = totalHeatMult = sparsityMult = totalFuelVessels = totalCooling = totalHeat = netHeat = totalIrradiation = functionalBlocks = 0;
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
        int maxDistance = getInternalVolume();//the algorithm requires a max search distance. Rather than changing that, I'll just be lazy and give it a big enough number
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
                    if(!contains(block.x+dx, block.y+dy, block.z+dz))continue;
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
    public OverhaulMSR blankCopy(){
        return new OverhaulMSR(configuration, getInternalWidth(), getInternalHeight(), getInternalDepth());
    }
    @Override
    public synchronized OverhaulMSR doCopy(){
        OverhaulMSR copy = blankCopy();
        forEachPosition((x, y, z) -> {
            copy.setBlock(x, y, z, getBlock(x, y, z));
        });
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
        copy.totalOutput.addAll(totalOutput);
        copy.totalTotalOutput = totalTotalOutput;
        copy.shutdownFactor = shutdownFactor;
        return copy;
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
    protected void getExtraParts(ArrayList<PartCount> parts){}
    @Override
    public String getDescriptionTooltip(){
        return "Overhaul MSRs are Molten Salt Fission reactors in NuclearCraft: Overhauled";
    }
    @Override
    public void getSuggestors(ArrayList<Suggestor> suggestors){
        suggestors.add(new Suggestor<OverhaulMSR>("Fuel Vessel Suggestor", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulMSR>("Efficiency", true, true){
                    @Override
                    protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                        return main.totalEfficiency-other.totalEfficiency;
                    }
                });
                priorities.add(new Priority<OverhaulMSR>("Output", true, true){
                    @Override
                    protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                        return main.totalTotalOutput-other.totalTotalOutput;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests adding Fuel vessels with moderators to increase efficiency and output";
            }
            @Override
            public void generateSuggestions(OverhaulMSR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> baseVessels = new ArrayList<>();
                multiblock.getAvailableBlocks(baseVessels);
                for(Iterator<Block> it = baseVessels.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isFuelVessel())it.remove();
                }
                ArrayList<Block> vessels = new ArrayList<>();
                for(Block baseVessel : baseVessels){
                    boolean hasPinned = false;
                    for(Fuel fuel : baseVessel.template.fuels){
                        if(Pinnable.isPinned(fuel)){
                            hasPinned = true;
                            break;
                        }
                    }
                    for(Fuel fuel : baseVessel.template.fuels){
                        if(hasPinned&&!Pinnable.isPinned(fuel))continue;
                        Block vessel = baseVessel.copy();
                        vessel.fuel = fuel;
                        vessels.add(vessel);
                    }
                }
                ArrayList<Block> moderators = new ArrayList<>();
                multiblock.getAvailableBlocks(moderators);
                for(Iterator<Block> it = moderators.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isModerator())it.remove();
                }
                int[] vesselCount = new int[1];
                multiblock.forEachInternalPosition((x, y, z) -> {
                    Block b = multiblock.getBlock(x,y,z);
                    if(b!=null&&b.isFuelVessel()){
                        vesselCount[0]++;
                    }
                });
                suggestor.setCount((multiblock.getInternalVolume()-vesselCount[0])*vessels.size()*moderators.size());
                for(Block vessel : vessels){
                    for(Block moderator : moderators){
                        multiblock.forEachInternalPosition((x, y, z) -> {
                            Block was = multiblock.getBlock(x, y, z);
                            if(was!=null&&was.isFuelVessel())return;
                            ArrayList<Action> actions = new ArrayList<>();
                            Block ve = (Block)vessel.newInstance(x, y, z);
                            ve.fuel = vessel.fuel;
                            actions.add(new SetblockAction(x, y, z, ve));
                            SetblocksAction multi = new SetblocksAction(moderator);
                            DIRECTION:for(Direction d : Direction.values()){
                                ArrayList<int[]> toSet = new ArrayList<>();
                                boolean yep = false;
                                for(int i = 1; i<=getSpecificConfiguration().settings.neutronReach+1; i++){
                                    int X = x+d.x*i;
                                    int Y = y+d.y*i;
                                    int Z = z+d.z*i;
                                    if(X==0||Y==0||Z==0||X==OverhaulMSR.this.x+1||Y==OverhaulMSR.this.y+1||Z==OverhaulMSR.this.z+1)break;//that's the casing
                                    if(!multiblock.contains(X, Y, Z))break;//end of the line
                                    Block b = multiblock.getBlock(X, Y, Z);
                                    if(b!=null){
                                        if(b.isModerator())continue;//already a moderator
                                        if(b.isFuelVessel()){
                                            yep = true;
                                            break;
                                        }
                                    }
                                    if(i<=getSpecificConfiguration().settings.neutronReach){
                                        toSet.add(new int[]{X,Y,Z});
                                    }
                                }
                                if(yep){
                                    for(int[] b : toSet)multi.add(b[0], b[1], b[2]);
                                }
                            }
                            if(!multi.isEmpty())actions.add(multi);
                            if(suggestor.acceptingSuggestions())suggestor.suggest(new Suggestion("Add "+vessel.getName()+(multi.isEmpty()?"":" with "+moderator.getName()), actions, priorities, vessel.getTexture(), moderator.getTexture()));
                        });
                    }
                }
            }
        });
        suggestors.add(new Suggestor<OverhaulMSR>("Moderator Line Upgrader", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulMSR>("Vessel Count", true, true){
                    @Override
                    protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                        return main.totalFuelVessels-other.totalFuelVessels;
                    }
                });
                priorities.add(new Priority<OverhaulMSR>("Efficiency", true, true){
                    @Override
                    protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                        return (int) Math.round(main.totalEfficiency*10000-other.totalEfficiency*10000);
                    }
                });
                priorities.add(new Priority<OverhaulMSR>("Irradiation", true, true){
                    @Override
                    protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                        return main.totalIrradiation-other.totalIrradiation;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests changing moderator lines to increase efficiency or irradiation";
            }
            @Override
            public void generateSuggestions(OverhaulMSR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> moderators = new ArrayList<>();
                multiblock.getAvailableBlocks(moderators);
                for(Iterator<Block> it = moderators.iterator(); it.hasNext();){
                    Block block = it.next();
                    if(!block.isModerator())it.remove();
                }
                int count = 0;
                for(Block block : multiblock.getBlocks()){
                    if(block.isFuelVessel())count++;
                }
                suggestor.setCount(count*6*moderators.size());
                for(Block block : multiblock.getBlocks()){
                    if(!block.isFuelVessel())continue;
                    DIRECTION:for(Direction d : Direction.values()){
                        ArrayList<Block> line = new ArrayList<>();
                        int x = block.x;
                        int y = block.y;
                        int z = block.z;
                        for(int i = 0; i<getSpecificConfiguration().settings.neutronReach+1; i++){
                            x+=d.x;
                            y+=d.y;
                            z+=d.z;
                            Block b = multiblock.getBlock(x, y, z);
                            if(b==null){
                                suggestor.task.max--;
                                continue DIRECTION;
                            }
                            if(!b.isModerator()){
                                if(b.isFuelVessel()||b.isIrradiator()||b.isReflector())break;
                                suggestor.task.max--;
                                continue DIRECTION;
                            }
                            line.add(b);
                        }
                        if(line.size()>getSpecificConfiguration().settings.neutronReach){
                            suggestor.task.max--;
                            continue;
                        }//too long
                        for(Block mod : moderators){
                            ArrayList<Action> actions = new ArrayList<>();
                            for(Block b : line){
                                actions.add(new SetblockAction(b.x, b.y, b.z, mod.newInstance(b.x, b.y, b.z)));
                            }
                            suggestor.suggest(new Suggestion("Replace Moderator Line with "+StringUtil.superRemove(mod.getName(), " Moderator"), actions, priorities, mod.getTexture()));
                        }
                    }
                }
            }
        });
        suggestors.add(new Suggestor<OverhaulMSR>("Single Moderator Upgrader", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulMSR>("Vessel Count", true, true){
                    @Override
                    protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                        return main.totalFuelVessels-other.totalFuelVessels;
                    }
                });
                priorities.add(new Priority<OverhaulMSR>("Efficiency", true, true){
                    @Override
                    protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                        return (int) Math.round(main.totalEfficiency*10000-other.totalEfficiency*10000);
                    }
                });
                priorities.add(new Priority<OverhaulMSR>("Irradiation", true, true){
                    @Override
                    protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                        return main.totalIrradiation-other.totalIrradiation;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests changing single moderators to increase efficiency or irradiation";
            }
            @Override
            public void generateSuggestions(OverhaulMSR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> moderators = new ArrayList<>();
                multiblock.getAvailableBlocks(moderators);
                for(Iterator<Block> it = moderators.iterator(); it.hasNext();){
                    Block block = it.next();
                    if(!block.isModerator())it.remove();
                }
                int count = 0;
                for(Block b : multiblock.getBlocks()){
                    if(b.isModerator())count++;
                }
                suggestor.setCount(count*moderators.size());
                for(Block block : multiblock.getBlocks()){
                    if(!block.isModerator())continue;
                    for(Block b : moderators){
                        suggestor.suggest(new Suggestion("Upgrade Moderator from "+StringUtil.superRemove(block.getName(), " Moderator")+" to "+StringUtil.superRemove(b.getName(), " Moderator"), new SetblockAction(block.x, block.y, block.z, b.newInstance(block.x, block.y, block.z)), priorities, b.getTexture()));
                    }
                }
            }
        });
        suggestors.add(new Suggestor<OverhaulMSR>("Heater Suggestor", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulMSR>("Temperature", true, true){
                    @Override
                    protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                        return other.netHeat-main.netHeat;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests adding or replacing heaters to cool the reactor";
            }
            @Override
            public void generateSuggestions(OverhaulMSR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> heaters = new ArrayList<>();
                multiblock.getAvailableBlocks(heaters);
                for(Iterator<Block> it = heaters.iterator(); it.hasNext();){
                    Block block = it.next();
                    if(!block.isHeater())it.remove();
                    else block.heaterRecipe = block.template.heaterRecipes.get(0);
                }
                int[] count = new int[1];
                multiblock.forEachInternalPosition((x, y, z) -> {
                    Block block = multiblock.getBlock(x, y, z);
                    if(block==null||block.canBeQuickReplaced()){
                        count[0]++;
                    }
                });
                suggestor.setCount(count[0]*heaters.size());
                multiblock.forEachInternalPosition((x, y, z) -> {
                    for(Block newBlock : heaters){
                        Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            int oldCooling = 0;
                            if(block!=null&&block.isHeaterActive())oldCooling = block.heaterRecipe.stats.cooling;
                            int newCooling = newBlock.heaterRecipe.stats.cooling;
                            if(newCooling>oldCooling&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock.newInstance(x, y, z)), priorities, newBlock.getTexture()));
                            else suggestor.task.max--;
                        }
                    }
                });
            }
        });
    }
    @Override
    public boolean canBePlacedInCasingEdge(Block b){
        return b.isCasing()&&b.template.casing.edge;
    }
    @Override
    public boolean canBePlacedInCasingFace(Block b){
        return b.isCasing();
    }
    @Override
    public boolean canBePlacedWithinCasing(Block b){
        return !b.isCasing();
    }
    @Override
    public void buildDefaultCasing(){
        Block casing = null;
        Block window = null;
        Block controller = null;
        HashMap<NCPFElement, BlockElement> inPorts = new HashMap<>();
        HashMap<NCPFElement, BlockElement> outPorts = new HashMap<>();
        for(BlockElement template : getSpecificConfiguration().blocks){
            if(template.casing!=null&&template.casing.edge)casing = new Block(getConfiguration(), 0, 0, 0, template);
            if(template.casing!=null&&!template.casing.edge&&template.controller==null&&template.port==null&&template.neutronSource==null&&template.parent==null&&template.unToggled==null)window = new Block(getConfiguration(), 0, 0, 0, template);
            if(template.controller!=null)controller = new Block(getConfiguration(), 0, 0, 0, template);
        }
        for(Block block : getBlocks(true)){
            if(block.template.recipePorts!=null){
                inPorts.put(block.getRecipe(), block.template.recipePorts.input.block);
                outPorts.put(block.getRecipe(), block.template.recipePorts.output.block);
            }
        }
        final Block theCasing = casing;
        final Block theWindow = window==null?casing:window;
        final Block theController = controller;
        boolean[] hasPlacedTheController = new boolean[1];
        HashSet<NCPFElement> hasPlacedTheInputPort = new HashSet<>();
        HashSet<NCPFElement> hasPlacedTheOutputPort = new HashSet<>();
        for(Block block : getBlocks()){
            if(block.template.controller!=null)hasPlacedTheController[0] = true;
            if(block.template.parent!=null){
                if(block.isToggled()){
                    if(outPorts.containsKey(block.getRecipe()))hasPlacedTheOutputPort.add(block.getRecipe());
                }else{
                    if(inPorts.containsKey(block.getRecipe()))hasPlacedTheInputPort.add(block.getRecipe());
                }
            }
        }
        forEachCasingFacePosition((x, y, z) -> {
            Block b = getBlock(x, y, z);
            if(b!=null&&b.template.parent!=null&&!inPorts.containsKey(b.getRecipe())&&!outPorts.containsKey(b.getRecipe()))setBlock(x, y, z, null);
        });
        forEachCasingFacePosition((x, y, z) -> {
            if(getBlock(x, y, z)!=null){
                if(getBlock(x, y, z).template!=theCasing.template&&getBlock(x, y, z).template!=theWindow.template)return;
            }
            if(!hasPlacedTheController[0]){
                setBlock(x, y, z, theController);
                hasPlacedTheController[0] = true;
                return;
            }
            for(Iterator<NCPFElement> it = inPorts.keySet().iterator(); it.hasNext();){
                NCPFElement recipe = it.next();
                if(hasPlacedTheInputPort.contains(recipe))continue;
                BlockElement template = inPorts.get(recipe);
                setBlock(x, y, z, new Block(getConfiguration(), 0, 0, 0, template));
                getBlock(x, y, z).setRecipe(recipe);
                it.remove();
                return;
            }
            for(Iterator<NCPFElement> it = outPorts.keySet().iterator(); it.hasNext();){
                NCPFElement recipe = it.next();
                if(hasPlacedTheOutputPort.contains(recipe))continue;
                BlockElement template = outPorts.get(recipe);
                setBlock(x, y, z, new Block(getConfiguration(), 0, 0, 0, template));
                getBlock(x, y, z).setRecipe(recipe);
                getBlock(x, y, z).setToggled(true);
                it.remove();
                return;
            }
        });
        forEachCasingEdgePosition((x, y, z) -> {
            if(getBlock(x, y, z)!=null)return;
            setBlock(x, y, z, theCasing);
        });
        forEachCasingFacePosition((x, y, z) -> {
            if(getBlock(x, y, z)!=null)return;
            setBlock(x, y, z, theWindow);
        });
    }
    @Override
    public String getPreviewTexture(){
        return "multiblocks/overhaul_msr";
    }
    @Override
    public LiteMultiblock<OverhaulMSR> compile(){
        return null;
    }
    @Override
    public Design toDesign(){
        OverhaulMSRDesign design = new OverhaulMSRDesign(Core.project, x, y, z);
        forEachPosition((x, y, z) -> {
            Block block = getBlock(x, y, z);
            design.design[x][y][z] = block==null?null:block.template;
            if(block!=null){
                design.fuels[x][y][z] = block.fuel;
                design.irradiatorRecipes[x][y][z] = block.irradiatorRecipe;
                design.heaterRecipes[x][y][z] = block.heaterRecipe;
            }
        });
        return design;
    }
}