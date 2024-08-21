package net.ncplanner.plannerator.multiblock.overhaul.fissionsfr;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Direction;
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
import net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR.CompiledOverhaulSFRConfiguration;
import net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR.LiteOverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
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
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulSFRDesign;
public class OverhaulSFR extends CuboidalMultiblock<Block>{
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
    private HashMap<Block, Boolean> shieldsWere = new HashMap<>();//used for shield check
    private ArrayList<Block> cellsWereActive = new ArrayList<>();//used for shield check
    private HashMap<NCPFElement, BlockElement> missingInputPorts = new HashMap<>();
    private HashMap<NCPFElement, BlockElement> missingOutputPorts = new HashMap<>();
    private boolean hasInputVent, hasOutputVent;
    private int calcStep = 0;
    private int calcSubstep = 0;
    private int numControllers;
    private int missingCasings;
    private Task calcCasing;
    private Task openShields;
    private Task propogateFlux;
    private Task rePropogateFlux;
    private Task postFluxCalc;
    private Task calcHeatsinks;
    private Task initCells;
    private Task buildClusters;
    private Task calcClusters;
    private Task calcStats;
    private Task calcShutdown;
    private Task shutdownCalcCasing;
    private Task shutdownCloseShields;
    private Task shutdownPropogateFlux;
    private Task shutdownRePropogateFlux;
    private Task shutdownPostFluxCalc;
    private Task shutdownCalcHeatsinks;
    private Task shutdownInitCells;
    private Task shutdownBuildClusters;
    private Task shutdownCalcClusters;
    private Task shutdownCalcStats;
    private Task calcPartialShutdown;
    private Task partialShutdownCalcCasing;
    private Task partialShutdownResetShields;
    private Task partialShutdownPropogateFlux;
    private Task partialShutdownRePropogateFlux;
    private Task partialShutdownPostFluxCalc;
    private Task partialShutdownCalcHeatsinks;
    private Task partialShutdownInitCells;
    private Task partialShutdownBuildClusters;
    private Task partialShutdownCalcClusters;
    private Task partialShutdownCalcStats;
    private float offOutput;
    public final Queue<Decal> initialFluxDecals = new Queue<>();
    public final Queue<Decal> finalFluxDecals = new Queue<>();
    public final Queue<Decal> shutdownFluxDecals = new Queue<>();
    public OverhaulSFR(){
        this(null);
    }
    public OverhaulSFR(NCPFConfigurationContainer configuration){
        this(configuration, configuration==null?0:configuration.getConfiguration(OverhaulSFRConfiguration::new).boundSize(7), configuration==null?0:configuration.getConfiguration(OverhaulSFRConfiguration::new).boundSize(5), configuration==null?0:configuration.getConfiguration(OverhaulSFRConfiguration::new).boundSize(7), null);
    }
    public OverhaulSFR(NCPFConfigurationContainer configuration, int x, int y, int z, CoolantRecipe coolantRecipe){
        super(configuration, x, y, z);
        this.coolantRecipe = coolantRecipe==null?(exists()?getSpecificConfiguration().coolantRecipes.get(0):null):coolantRecipe;
    }
    @Override
    public OverhaulSFRConfiguration getSpecificConfiguration(){
        NCPFConfigurationContainer conf = getConfiguration();
        if(conf==null)return null;
        return conf.getConfiguration(OverhaulSFRConfiguration::new);
    }
    @Override
    public String getDefinitionName(){
        return "Overhaul SFR";
    }
    @Override
    public OverhaulSFR newInstance(NCPFConfigurationContainer configuration){
        return new OverhaulSFR(configuration);
    }
    @Override
    public Multiblock<Block> newInstance(NCPFConfigurationContainer configuration, int x, int y, int z){
        return new OverhaulSFR(configuration, x, y, z, null);
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
        propogateFlux = calculateTask.addSubtask(new Task("Propogating Neutron Flux"));
        rePropogateFlux = calculateTask.addSubtask(new Task("Re-propogating Neutron Flux"));
        postFluxCalc = calculateTask.addSubtask(new Task("Performing Post-Flux Calculations"));
        calcHeatsinks = calculateTask.addSubtask(new Task("Calculating Heatsinks"));
        initCells = calculateTask.addSubtask(new Task("Initializing Fuel Cells"));
        buildClusters = calculateTask.addSubtask(new Task("Building Clusters"));
        calcClusters = calculateTask.addSubtask(new Task("Calculating Clusters"));
        calcStats = calculateTask.addSubtask(new Task("Calculating Stats"));
        calcShutdown = calculateTask.addSubtask(new Task("Calculating Shutdown Factor"));
        shutdownCalcCasing = calculateTask.addSubtask(new Task("Calculating Casing"));
        shutdownCloseShields = calcShutdown.addSubtask(new Task("Closing Neutron Shields"));
        shutdownPropogateFlux = calcShutdown.addSubtask(new Task("Propogating Neutron Flux"));
        shutdownRePropogateFlux = calcShutdown.addSubtask(new Task("Re-propogating Neutron Flux"));
        shutdownPostFluxCalc = calcShutdown.addSubtask(new Task("Performing Post-Flux Calculations"));
        shutdownCalcHeatsinks = calcShutdown.addSubtask(new Task("Calculating Heatsinks"));
        shutdownInitCells = calcShutdown.addSubtask(new Task("Initializing Fuel Cells"));
        shutdownBuildClusters = calcShutdown.addSubtask(new Task("Building Clusters"));
        shutdownCalcClusters = calcShutdown.addSubtask(new Task("Calculating Clusters"));
        shutdownCalcStats = calcShutdown.addSubtask(new Task("Calculating Stats"));
        calcPartialShutdown = calculateTask.addSubtask(new Task("Calculating Partial Shutdown"));
        partialShutdownCalcCasing = calculateTask.addSubtask(new Task("Calculating Casing"));
        partialShutdownResetShields = calcPartialShutdown.addSubtask(new Task("Resetting Neutron Shields"));
        partialShutdownPropogateFlux = calcPartialShutdown.addSubtask(new Task("Propogating Neutron Flux"));
        partialShutdownRePropogateFlux = calcPartialShutdown.addSubtask(new Task("Re-propogating Neutron Flux"));
        partialShutdownPostFluxCalc = calcPartialShutdown.addSubtask(new Task("Performing Post-Flux Calculations"));
        partialShutdownCalcHeatsinks = calcPartialShutdown.addSubtask(new Task("Calculating Heatsinks"));
        partialShutdownInitCells = calcPartialShutdown.addSubtask(new Task("Initializing Fuel Cells"));
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
                hasInputVent = hasOutputVent = false;
                missingInputPorts.clear();
                missingOutputPorts.clear();
                forEachCasingPosition((x, y, z) -> {
                    Block block = getBlock(x, y, z);
                    if(block==null||!block.isCasing()){
                        missingCasings++;
                        if(addDecals)decals.enqueue(new MissingCasingDecal(x, y, z));
                    }
                    if(block!=null&&block.isCasing()){
                        if(block.template.coolantVent!=null&&block.template.coolantVent.output)hasOutputVent = true;
                        if(block.template.coolantVent!=null&&!block.template.coolantVent.output)hasInputVent = true;
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
                                    if(b.template.fuelCell!=null){
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
                cellsWereActive.clear();
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
            case 2://propogate neutron flux
                Queue<Decal> fluxDecals = new Queue<>();
                for(int i = 0; i<blocks.size(); i++){
                    Block block = blocks.get(i);
                    propogateNeutronFlux(block, false, fluxDecals, true);
                    propogateFlux.progress = i/(double)blocks.size();
                }
                for(Block block : blocks){
                    if(block.template.fuelCell!=null)fluxDecals.enqueue(new CellFluxDecal(block.x, block.y, block.z, block.neutronFlux, block.fuel==null?0:block.fuel.stats.criticality));
                }
                if(addDecals)for(Decal d : fluxDecals)decals.enqueue(d);
                propogateFlux.finish();
                calcStep++;
                return true;
            case 3://repropogate neutron flux
                calcSubstep++;
                rePropogateFlux.name = "Re-propogating Neutron Flux"+(calcSubstep>1?" ("+calcSubstep+")":"");
                int lastActive = 0;
                for(Block block : blocks){
                    boolean wasActive = block.isFuelCellActive();
                    block.hadFlux = block.neutronFlux;
                    Block source = block.source;
                    if(block.isFuelCell()||block.isModerator()||block.isShield()||block.isReflector()||block.isIrradiator())block.clearData();
                    block.source = source;
                    if(wasActive)lastActive++;
                    block.wasActive = wasActive;
                }
                fluxDecals = new Queue<>();
                for(int i = 0; i<blocks.size(); i++){
                    Block block = blocks.get(i);
                    propogateNeutronFlux(block, false, fluxDecals, false);
                    rePropogateFlux.progress = i/(double)blocks.size();
                }
                for(Block block : blocks){
                    if(block.template.fuelCell!=null)fluxDecals.enqueue(new CellFluxDecal(block.x, block.y, block.z, block.neutronFlux, block.fuel==null?0:block.fuel.stats.criticality));
                }
                if(addDecals)for(Decal d : fluxDecals)decals.enqueue(d);
                int nowActive = 0;
                for(Block block : blocks){
                    if(block.isFuelCellActive())nowActive++;
                    if(block.isFuelCell()&&!block.wasActive){
                        block.neutronFlux = block.hadFlux;
                    }
                }
                if(nowActive!=lastActive)return true;
                rePropogateFlux.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 4://post flux calc
                for(int i = 0; i<blocks.size(); i++){
                    Block block = blocks.get(i);
                    if(block.isFuelCell())postFluxCalc(block, addDecals);
                    postFluxCalc.progress = i/(double)blocks.size();
                }
                postFluxCalc.finish();
                calcStep++;
                return true;
            case 5://heatsinks
                calcSubstep++;
                boolean somethingChanged = false;
                calcHeatsinks.name = "Calculating Heatsinks"+(calcSubstep>1?" ("+calcSubstep+")":"");
                for(int i = 0; i<blocks.size(); i++){
                    if(calculateHeatsink(blocks.get(i), addDecals))somethingChanged = true;
                    calcHeatsinks.progress = i/(double)blocks.size();
                }
                if(somethingChanged)return true;
                calcHeatsinks.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 6://init cells
                for(int i = 0; i<blocks.size(); i++){
                    Block block = blocks.get(i);
                    if(block.isFuelCell()&&block.fuel!=null){
                        float criticalityModifier = (float) (1/(1+MathUtil.exp(2*(block.neutronFlux-2*block.fuel.stats.criticality))));
                        block.efficiency = block.fuel.stats.efficiency*block.positionalEfficiency*(block.source==null?1:block.source.template.neutronSource.efficiency)*criticalityModifier;
                        if(addDecals)decals.enqueue(new BlockValidDecal(block.x, block.y, block.z));
                    }
                    initCells.progress = i/(double)blocks.size();
                }
                initCells.finish();
                calcStep++;
                return true;
            case 7://build clusters
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
            case 8://calculate clusters
                synchronized(clusters){
                    for(int i = 0; i<clusters.size(); i++){
                        Cluster cluster = clusters.get(i);
                        int fuelCells = 0;
                        for(int j = 0; j<cluster.blocks.size(); j++){
                            Block b = cluster.blocks.get(j);
                            if(b.isFuelCellActive()){
                                fuelCells++;
                                cluster.totalOutput+=b.fuel.stats.heat*b.efficiency;
                                cluster.efficiency+=b.efficiency;
                                cluster.totalHeat+=b.moderatorLines*b.fuel.stats.heat;
                                cluster.heatMult+=b.moderatorLines;
                            }
                            if(b.isHeatsinkActive()){
                                cluster.totalCooling+=b.template.heatsink.cooling;
                            }
                            if(b.isShieldActive()){
                                cluster.totalOutput+=b.template.neutronShield.heatPerFlux*b.neutronFlux*b.template.neutronShield.efficiency;
                                cluster.totalHeat+=b.template.neutronShield.heatPerFlux*b.neutronFlux;
                            }
                            if(b.isIrradiatorActive()){
                                cluster.irradiation+=b.neutronFlux;
                                cluster.totalHeat+=b.irradiatorRecipe.stats.heat*b.neutronFlux;
                            }
                            calcClusters.progress = (i+j/(double)cluster.blocks.size())/(double)clusters.size();
                        }
                        cluster.efficiency/=fuelCells;
                        cluster.heatMult/=fuelCells;
                        if(Double.isNaN(cluster.efficiency))cluster.efficiency = 0;
                        if(Double.isNaN(cluster.heatMult))cluster.heatMult = 0;
                        cluster.netHeat = cluster.totalHeat-cluster.totalCooling;
                        if(cluster.totalCooling==0)cluster.coolingPenaltyMult = 1;
                        else cluster.coolingPenaltyMult = Math.min(1, (cluster.totalHeat+getSpecificConfiguration().settings.coolingEfficiencyLeniency)/(float)cluster.totalCooling);
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
                        if(cluster.totalHeat==0)cluster.isConnectedToWall = true;
                        calcClusters.progress = (i+1)/(double)clusters.size();
                    }
                }
                calcClusters.finish();
                calcStep++;
                return true;
            case 9://calculate stats
                totalEfficiency/=totalFuelCells;
                totalHeatMult/=totalFuelCells;
                if(Double.isNaN(totalEfficiency))totalEfficiency = 0;
                if(Double.isNaN(totalHeatMult))totalHeatMult = 0;
                functionalBlocks = 0;
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.isFunctional())functionalBlocks++;
                    calcStats.progress = i/(double)allBlocks.size();
                }
                int volume = getInternalVolume();
                sparsityMult = (float) (functionalBlocks/(float)volume>=getSpecificConfiguration().settings.sparsityPenaltyThreshold?1:getSpecificConfiguration().settings.sparsityPenaltyMultiplier+(1-getSpecificConfiguration().settings.sparsityPenaltyMultiplier)*Math.sin(Math.PI*functionalBlocks/(2*volume*getSpecificConfiguration().settings.sparsityPenaltyThreshold)));
                totalOutput*=sparsityMult;
                totalEfficiency*=sparsityMult;
                totalOutput/=coolantRecipe.stats.heat/coolantRecipe.stats.outputRatio;
                calcStats.finish();
                calcStep++;
                return true;
//</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="Shutdown Factor">
            case 10://clear data & calculate casing
                cellsWereActive.clear();
                for(Block b : getBlocks())if(b!=null&&b.isFuelCellActive())cellsWereActive.add(b);
                clearData(allBlocks);
                validate();
                numControllers = missingCasings = 0;
                hasInputVent = hasOutputVent = false;
                missingInputPorts.clear();
                missingOutputPorts.clear();
                forEachCasingPosition((x, y, z) -> {
                    Block block = getBlock(x, y, z);
                    if(block==null||!block.isCasing()){
                        missingCasings++;
                        if(addDecals)decals.enqueue(new MissingCasingDecal(x, y, z));
                    }
                    if(block!=null&&block.isCasing()){
                        if(block.template.coolantVent!=null&&block.template.coolantVent.output)hasOutputVent = true;
                        if(block.template.coolantVent!=null&&!block.template.coolantVent.output)hasInputVent = true;
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
                                    if(b.template.fuelCell!=null){
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
            case 11://close shields
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.template.neutronShield!=null)block.setToggled(true);;
                    shutdownCloseShields.progress = i/(double)allBlocks.size();
                }
                shutdownCloseShields.finish();
                calcStep++;
                return true;
            case 12://propogate neutron flux
                fluxDecals = new Queue<>();
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    propogateNeutronFlux(block, cellsWereActive.contains(block), fluxDecals, true);
                    shutdownPropogateFlux.progress = i/(double)allBlocks.size();
                }
                for(Block block : allBlocks){
                    if(block.template.fuelCell!=null)fluxDecals.enqueue(new CellFluxDecal(block.x, block.y, block.z, block.neutronFlux, block.fuel==null?0:block.fuel.stats.criticality));
                }
                if(addDecals)for(Decal d : fluxDecals)decals.enqueue(d);
                shutdownPropogateFlux.finish();
                calcStep++;
                return true;
            case 13://repropogate neutron flux
                calcSubstep++;
                shutdownRePropogateFlux.name = "Re-propogating Neutron Flux"+(calcSubstep>1?" ("+calcSubstep+")":"");
                lastActive = 0;
                for(Block block : allBlocks){
                    boolean wasActive = block.isFuelCellActive();
                    block.hadFlux = block.neutronFlux;
                    Block source = block.source;
                    if(block.isFuelCell()||block.isModerator()||block.isShield()||block.isReflector()||block.isIrradiator())block.clearData();
                    block.source = source;
                    if(wasActive)lastActive++;
                    block.wasActive = wasActive;
                }
                fluxDecals = new Queue<>();
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    propogateNeutronFlux(block, cellsWereActive.contains(block), fluxDecals, false);
                    shutdownRePropogateFlux.progress = i/(double)allBlocks.size();
                }
                for(Block block : allBlocks){
                    if(block.template.fuelCell!=null)fluxDecals.enqueue(new CellFluxDecal(block.x, block.y, block.z, block.neutronFlux, block.fuel==null?0:block.fuel.stats.criticality));
                }
                for(Decal d : fluxDecals)shutdownFluxDecals.enqueue(d);
                if(addDecals)for(Decal d : fluxDecals)decals.enqueue(d);
                nowActive = 0;
                for(Block block : allBlocks){
                    if(block.isFuelCellActive())nowActive++;
                    if(block.isFuelCell()&&!block.wasActive){
                        block.neutronFlux = block.hadFlux;
                    }
                }
                if(nowActive!=lastActive)return true;
                shutdownRePropogateFlux.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 14://post flux calc
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.isFuelCell())postFluxCalc(block, addDecals);
                    shutdownPostFluxCalc.progress = i/(double)allBlocks.size();
                }
                shutdownPostFluxCalc.finish();
                calcStep++;
                return true;
            case 15://heatsinks
                calcSubstep++;
                somethingChanged = false;
                shutdownCalcHeatsinks.name = "Calculating Heatsinks"+(calcSubstep>1?" ("+calcSubstep+")":"");
                for(int i = 0; i<allBlocks.size(); i++){
                    if(calculateHeatsink(allBlocks.get(i), addDecals))somethingChanged = true;
                    shutdownCalcHeatsinks.progress = i/(double)allBlocks.size();
                }
                if(somethingChanged)return true;
                shutdownCalcHeatsinks.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 16://init cells
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.isFuelCell()&&block.fuel!=null){
                        float criticalityModifier = (float) (1/(1+MathUtil.exp(2*(block.neutronFlux-2*block.fuel.stats.criticality))));
                        block.efficiency = block.fuel.stats.efficiency*block.positionalEfficiency*(block.source==null?1:block.source.template.neutronSource.efficiency)*criticalityModifier;
                        if(addDecals)decals.enqueue(new BlockValidDecal(block.x, block.y, block.z));
                    }
                    shutdownInitCells.progress = i/(double)allBlocks.size();
                }
                shutdownInitCells.finish();
                calcStep++;
                return true;
            case 17://build clusters
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
            case 18://calculate clusters
                synchronized(clusters){
                    for(int i = 0; i<clusters.size(); i++){
                        Cluster cluster = clusters.get(i);
                        int fuelCells = 0;
                        for(int j = 0; j<cluster.blocks.size(); j++){
                            Block b = cluster.blocks.get(j);
                            if(b.isFuelCellActive()){
                                fuelCells++;
                                cluster.totalOutput+=b.fuel.stats.heat*b.efficiency;
                                cluster.efficiency+=b.efficiency;
                                cluster.totalHeat+=b.moderatorLines*b.fuel.stats.heat;
                                cluster.heatMult+=b.moderatorLines;
                            }
                            if(b.isHeatsinkActive()){
                                cluster.totalCooling+=b.template.heatsink.cooling;
                            }
                            if(b.isShieldActive()){
                                cluster.totalOutput+=b.template.neutronShield.heatPerFlux*b.neutronFlux*b.template.neutronShield.efficiency;
                                cluster.totalHeat+=b.template.neutronShield.heatPerFlux*b.neutronFlux;
                            }
                            if(b.isIrradiatorActive()){
                                cluster.irradiation+=b.neutronFlux;
                                cluster.totalHeat+=b.irradiatorRecipe.stats.heat*b.neutronFlux;
                            }
                            shutdownCalcClusters.progress = (i+j/(double)cluster.blocks.size())/(double)clusters.size();
                        }
                        cluster.efficiency/=fuelCells;
                        cluster.heatMult/=fuelCells;
                        if(Double.isNaN(cluster.efficiency))cluster.efficiency = 0;
                        if(Double.isNaN(cluster.heatMult))cluster.heatMult = 0;
                        cluster.netHeat = cluster.totalHeat-cluster.totalCooling;
                        if(cluster.totalCooling==0)cluster.coolingPenaltyMult = 1;
                        else cluster.coolingPenaltyMult = Math.min(1, (cluster.totalHeat+getSpecificConfiguration().settings.coolingEfficiencyLeniency)/(float)cluster.totalCooling);
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
                        if(cluster.totalHeat==0)cluster.isConnectedToWall = true;
                        shutdownCalcClusters.progress = (i+1)/(double)clusters.size();
                    }
                }
                shutdownCalcClusters.finish();
                calcStep++;
                return true;
            case 19://calculate stats
                totalEfficiency/=totalFuelCells;
                totalHeatMult/=totalFuelCells;
                if(Double.isNaN(totalEfficiency))totalEfficiency = 0;
                if(Double.isNaN(totalHeatMult))totalHeatMult = 0;
                functionalBlocks = 0;
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.isFunctional())functionalBlocks++;
                    shutdownCalcStats.progress = i/(double)allBlocks.size();
                }
                volume = getInternalVolume();
                sparsityMult = (float) (functionalBlocks/(float)volume>=getSpecificConfiguration().settings.sparsityPenaltyThreshold?1:getSpecificConfiguration().settings.sparsityPenaltyMultiplier+(1-getSpecificConfiguration().settings.sparsityPenaltyMultiplier)*Math.sin(Math.PI*functionalBlocks/(2*volume*getSpecificConfiguration().settings.sparsityPenaltyThreshold)));
                totalOutput*=sparsityMult;
                totalEfficiency*=sparsityMult;
                totalOutput/=coolantRecipe.stats.heat/coolantRecipe.stats.outputRatio;
                shutdownCalcStats.finish();
                calcShutdown.finish();
                offOutput = totalOutput;
                calcStep++;
                return true;
//</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="Partial Shutdown">
            case 20://clear data & calculate casing
                clearData(allBlocks);
                validate();
                numControllers = missingCasings = 0;
                hasInputVent = hasOutputVent = false;
                missingInputPorts.clear();
                missingOutputPorts.clear();
                forEachCasingPosition((x, y, z) -> {
                    Block block = getBlock(x, y, z);
                    if(block==null||!block.isCasing()){
                        missingCasings++;
                        if(addDecals)decals.enqueue(new MissingCasingDecal(x, y, z));
                    }
                    if(block!=null&&block.isCasing()){
                        if(block.template.coolantVent!=null&&block.template.coolantVent.output)hasOutputVent = true;
                        if(block.template.coolantVent!=null&&!block.template.coolantVent.output)hasInputVent = true;
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
                                    if(b.isFuelCell()){
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
            case 21://reset shields
                for(Block b : shieldsWere.keySet()){
                    b.setToggled(shieldsWere.get(b));
                }
                partialShutdownResetShields.finish();
                calcStep++;
                return true;
            case 22://propogate neutron flux
                fluxDecals = new Queue<>();
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    propogateNeutronFlux(block, cellsWereActive.contains(block), fluxDecals, true);
                    partialShutdownPropogateFlux.progress = i/(double)allBlocks.size();
                }
                for(Block block : allBlocks){
                    if(block.isFuelCell())fluxDecals.enqueue(new CellFluxDecal(block.x,block.y,block.z,block.neutronFlux,block.fuel==null?0:block.fuel.stats.criticality));
                }
                for(Decal d : fluxDecals)initialFluxDecals.enqueue(d);
                if(addDecals)for(Decal d : fluxDecals)decals.enqueue(d);
                partialShutdownPropogateFlux.finish();
                calcStep++;
                return true;
            case 23://repropogate neutron flux
                calcSubstep++;
                partialShutdownRePropogateFlux.name = "Re-propogating Neutron Flux"+(calcSubstep>1?" ("+calcSubstep+")":"");
                lastActive = 0;
                for(Block block : allBlocks){
                    boolean wasActive = block.isFuelCellActive();
                    block.hadFlux = block.neutronFlux;
                    Block source = block.source;
                    if(block.isFuelCell()||block.isModerator()||block.isShield()||block.isReflector()||block.isIrradiator())block.clearData();
                    block.source = source;
                    if(wasActive)lastActive++;
                    block.wasActive = wasActive;
                }
                fluxDecals = new Queue<>();
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    propogateNeutronFlux(block, cellsWereActive.contains(block), fluxDecals, false);
                    partialShutdownRePropogateFlux.progress = i/(double)allBlocks.size();
                }
                for(Block block : allBlocks){
                    if(block.isFuelCell())fluxDecals.enqueue(new CellFluxDecal(block.x,block.y,block.z,block.neutronFlux,block.fuel==null?0:block.fuel.stats.criticality));
                }
                for(Decal d : fluxDecals)finalFluxDecals.enqueue(d);
                if(addDecals)for(Decal d : fluxDecals)decals.enqueue(d);
                nowActive = 0;
                for(Block block : allBlocks){
                    if(block.isFuelCellActive())nowActive++;
                    if(block.isFuelCell()&&!block.wasActive){
                        block.neutronFlux = block.hadFlux;
                    }
                }
                if(nowActive!=lastActive)return true;
                partialShutdownRePropogateFlux.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 24://post flux calc
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.isFuelCell())postFluxCalc(block, addDecals);
                    partialShutdownPostFluxCalc.progress = i/(double)allBlocks.size();
                }
                partialShutdownPostFluxCalc.finish();
                calcStep++;
                return true;
            case 25://heatsinks
                calcSubstep++;
                somethingChanged = false;
                partialShutdownCalcHeatsinks.name = "Calculating Heatsinks"+(calcSubstep>1?" ("+calcSubstep+")":"");
                for(int i = 0; i<allBlocks.size(); i++){
                    if(calculateHeatsink(allBlocks.get(i), addDecals))somethingChanged = true;
                    partialShutdownCalcHeatsinks.progress = i/(double)allBlocks.size();
                }
                if(somethingChanged)return true;
                partialShutdownCalcHeatsinks.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 26://init cells
                for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.isFuelCell()&&block.fuel!=null){
                        float criticalityModifier = (float) (1/(1+MathUtil.exp(2*(block.neutronFlux-2*block.fuel.stats.criticality))));
                        block.efficiency = block.fuel.stats.efficiency*block.positionalEfficiency*(block.source==null?1:block.source.template.neutronSource.efficiency)*criticalityModifier;
                        if(addDecals)decals.enqueue(new BlockValidDecal(block.x, block.y, block.z));
                    }
                    partialShutdownInitCells.progress = i/(double)allBlocks.size();
                }
                partialShutdownInitCells.finish();
                calcStep++;
                return true;
            case 27://build clusters
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
            case 28://calculate clusters
                synchronized(clusters){
                    for(int i = 0; i<clusters.size(); i++){
                        Cluster cluster = clusters.get(i);
                        int fuelCells = 0;
                        for(int j = 0; j<cluster.blocks.size(); j++){
                            Block b = cluster.blocks.get(j);
                            if(b.isFuelCellActive()){
                                fuelCells++;
                                cluster.totalOutput+=b.fuel.stats.heat*b.efficiency;
                                cluster.efficiency+=b.efficiency;
                                cluster.totalHeat+=b.moderatorLines*b.fuel.stats.heat;
                                cluster.heatMult+=b.moderatorLines;
                            }
                            if(b.isHeatsinkActive()){
                                cluster.totalCooling+=b.template.heatsink.cooling;
                            }
                            if(b.isShieldActive()){
                                cluster.totalOutput+=b.template.neutronShield.heatPerFlux*b.neutronFlux*b.template.neutronShield.efficiency;
                                cluster.totalHeat+=b.template.neutronShield.heatPerFlux*b.neutronFlux;
                            }
                            if(b.isIrradiatorActive()){
                                cluster.irradiation+=b.neutronFlux;
                                cluster.totalHeat+=b.irradiatorRecipe.stats.heat*b.neutronFlux;
                            }
                            partialShutdownCalcClusters.progress = (i+j/(double)cluster.blocks.size())/(double)clusters.size();
                        }
                        cluster.efficiency/=fuelCells;
                        cluster.heatMult/=fuelCells;
                        if(Double.isNaN(cluster.efficiency))cluster.efficiency = 0;
                        if(Double.isNaN(cluster.heatMult))cluster.heatMult = 0;
                        cluster.netHeat = cluster.totalHeat-cluster.totalCooling;
                        if(cluster.totalCooling==0)cluster.coolingPenaltyMult = 1;
                        else cluster.coolingPenaltyMult = Math.min(1, (cluster.totalHeat+getSpecificConfiguration().settings.coolingEfficiencyLeniency)/(float)cluster.totalCooling);
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
                        if(cluster.totalHeat==0)cluster.isConnectedToWall = true;
                        partialShutdownCalcClusters.progress = (i+1)/(double)clusters.size();
                    }
                }
                partialShutdownCalcClusters.finish();
                calcStep++;
                return true;
            case 29://calculate stats
                totalEfficiency/=totalFuelCells;
                totalHeatMult/=totalFuelCells;
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
                    partialShutdownCalcStats.progress = i/(double)allBlocks.size()/2d;
                }
                BLOCK:for(int i = 0; i<allBlocks.size(); i++){
                    Block block = allBlocks.get(i);
                    if(block.isFunctional())functionalBlocks++;
                    if(block.template.parent!=null)continue;
                    if(block.getRecipe()!=null){
                        if(!inputPortRecipes.contains(block.getRecipe()))missingInputPorts.put(block.getRecipe(), block.template);
                        if(!outputPortRecipes.contains(block.getRecipe()))missingOutputPorts.put(block.getRecipe(), block.template);
                    }
                    partialShutdownCalcStats.progress = 0.5+i/(double)allBlocks.size()/2;
                }
                volume = getInternalVolume();
                sparsityMult = (float) (functionalBlocks/(float)volume>=getSpecificConfiguration().settings.sparsityPenaltyThreshold?1:getSpecificConfiguration().settings.sparsityPenaltyMultiplier+(1-getSpecificConfiguration().settings.sparsityPenaltyMultiplier)*Math.sin(Math.PI*functionalBlocks/(2*volume*getSpecificConfiguration().settings.sparsityPenaltyThreshold)));
                totalOutput*=sparsityMult;
                totalEfficiency*=sparsityMult;
                totalOutput/=coolantRecipe.stats.heat/coolantRecipe.stats.outputRatio;
                partialShutdownCalcStats.finish();
                calcPartialShutdown.finish();
                shutdownFactor = 1-(offOutput/totalOutput);
                calcStep = 0;
                return false;
//</editor-fold>
            default:
                throw new IllegalStateException("Invalid calculation step: "+calcStep+"!");
        }
    }
    public void propogateNeutronFlux(Block that, boolean force, Queue<Decal> fluxDecals, boolean initialPropogation){
        if(!that.isFuelCell())return;
        if(that.fuel==null)return;
        if(!initialPropogation&&!that.wasActive)return;
        if(!force&&!that.isPrimed()&&that.neutronFlux<that.fuel.stats.criticality)return;
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
                if(block.isFuelCell()){
                    if(length==0)break;
                    if(block.fuel==null)break;
                    block.neutronFlux+=flux;
                    block.moderatorLines++;
                    if(flux>0)block.positionalEfficiency+=efficiency/length;
                    int f = 0;
                    for(int j = 1; j<i; j++){
                        Block b = getBlock(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j);
                        f+=b.template.moderator==null?0:b.template.moderator.flux;//could be a shield
                        fluxDecals.enqueue(new OverhaulModeratorLineDecal(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j, d, f, efficiency/length));
                    }
                    fluxDecals.enqueue(new AdjacentModeratorLineDecal(that.x, that.y, that.z, d, efficiency/length));
                    propogateNeutronFlux(block, false, fluxDecals, initialPropogation);
                    break;
                }
                if(block.isReflector()){
                    if(length==0)break;
                    if(length>getSpecificConfiguration().settings.neutronReach/2)break;
                    that.neutronFlux+=flux*2*block.template.reflector.reflectivity;
                    if(flux>0)that.positionalEfficiency+=efficiency/length*block.template.reflector.efficiency;
                    that.moderatorLines++;
                    int f = 0;
                    for(int j = 1; j<i; j++){
                        Block b = getBlock(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j);
                        if(b.template.moderator!=null)f+=b.template.moderator.flux;
                        fluxDecals.enqueue(new OverhaulModeratorLineDecal(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j, d, f, efficiency/length));
                    }
                    f = 0;
                    for(int j = i-1; j>=1; j--){
                        Block b = getBlock(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j);
                        if(b.template.moderator!=null)f+=b.template.moderator.flux*block.template.reflector.reflectivity;
                        fluxDecals.enqueue(new OverhaulModeratorLineDecal(that.x+d.x*j, that.y+d.y*j, that.z+d.z*j, d.getOpposite(), (int)(flux*block.template.reflector.reflectivity)+f, efficiency/length));
                    }
                    fluxDecals.enqueue(new AdjacentModeratorLineDecal(that.x, that.y, that.z, d, efficiency/length));
                    fluxDecals.enqueue(new ReflectorAdjacentModeratorLineDecal(block.x, block.y, block.z, d.getOpposite()));
                    break;
                }
                if(block.isIrradiator()){
                    if(length==0)break;
                    if(block.irradiatorRecipe==null)break;
                    that.moderatorLines++;
                    if(flux>0)that.positionalEfficiency+=efficiency/length*block.irradiatorRecipe.stats.efficiency;
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
        if(!that.isFuelCellActive())return;
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
                if(block.isFuelCellActive()){
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
     * Calculates the heatsink
     * @param block the block
     * @param addDecals whether or not to add decals
     * @return <code>true</code> if the heatsink state has changed
     */
    public boolean calculateHeatsink(Block block, boolean addDecals){
        if(!block.isHeatsink())return false;
        boolean wasValid = block.heatsinkValid;
        for(NCPFPlacementRule rule : block.getRules()){
            if(!rule.isValid(block, this)){
                if(block.heatsinkValid&&addDecals)decals.enqueue(new BlockInvalidDecal(block.x, block.y, block.z));
                block.heatsinkValid = false;
                return wasValid!=block.heatsinkValid;
            }
        }
        if(!block.heatsinkValid&&addDecals)decals.enqueue(new BlockValidDecal(block.x, block.y, block.z));
        block.heatsinkValid = true;
        return wasValid!=block.heatsinkValid;
    }
    @Override
    public FormattedText getTooltip(boolean full){
        if(this.showDetails!=null)full = this.showDetails;
        synchronized(clusters){
            int validClusters = 0;
            for(Cluster c : clusters){
                if(c.isValid())validClusters++;
            }
            FormattedText text = new FormattedText();
            if(numControllers<1)text.addText("No controller!", Core.theme.getTooltipInvalidTextColor());
            if(numControllers>1)text.addText("Too many controllers!", Core.theme.getTooltipInvalidTextColor());
            if(missingCasings>0)text.addText("Casing incomplete! (Missing "+missingCasings+")", Core.theme.getTooltipInvalidTextColor());
            if(!hasInputVent)text.addText("Missing input vent!", Core.theme.getTooltipInvalidTextColor());
            if(!hasOutputVent)text.addText("Missing output vent!", Core.theme.getTooltipInvalidTextColor());
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
            text.addText("Total output: "+totalOutput+" mb/t of whatever "+coolantRecipe.getDisplayName()+" gets turned into\n"
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
    public OverhaulMSR convertToMSR() throws MissingConfigurationEntryException{
        OverhaulMSR msr = new OverhaulMSR(configuration, getInternalWidth(), getInternalHeight(), getInternalDepth());
        for(Block b : getBlocks(true)){
            msr.setBlockExact(b.x, b.y, b.z, b.convertToMSR());
        }
        msr.metadata.putAll(metadata);
        return msr;
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
            return "Total output: "+Math.round(totalOutput)+"\n"
                + "Efficiency: "+MathUtil.percent(efficiency, 0)+"\n"
                + "Total Heating: "+totalHeat+"H/t\n"
                + "Total Cooling: "+totalCooling+"H/t\n"
                + "Net Heating: "+netHeat+"H/t\n"
                + "Heat Multiplier: "+MathUtil.percent(heatMult, 0)+"\n"
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
    public OverhaulSFR blankCopy(){
        return new OverhaulSFR(configuration, getInternalWidth(), getInternalHeight(), getInternalDepth(), coolantRecipe);
    }
    @Override
    public OverhaulSFR doCopy(){
        OverhaulSFR copy = blankCopy();
        forEachPosition((x, y, z) -> {
            copy.setBlock(x, y, z, getBlock(x, y, z));
        });
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
    public String getGeneralName(){
        return "Reactor";
    }
    @Override
    public boolean isCompatible(Multiblock<Block> other){
        return ((OverhaulSFR)other).coolantRecipe==coolantRecipe;
    }
    @Override
    protected void getExtraParts(ArrayList<PartCount> parts){}
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
                ArrayList<Block> baseCells = new ArrayList<>();
                multiblock.getAvailableBlocks(baseCells);
                for(Iterator<Block> it = baseCells.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isFuelCell())it.remove();
                }
                ArrayList<Block> cells = new ArrayList<>();
                for(Block baseCell : baseCells){
                    boolean hasPinned = false;
                    for(Fuel fuel : baseCell.template.fuels){
                        if(Pinnable.isPinned(fuel)){
                            hasPinned = true;
                            break;
                        }
                    }
                    for(Fuel fuel : baseCell.template.fuels){
                        if(hasPinned&&!Pinnable.isPinned(fuel))continue;
                        Block cell = baseCell.copy();
                        cell.fuel = fuel;
                        cells.add(cell);
                    }
                }
                ArrayList<Block> moderators = new ArrayList<>();
                multiblock.getAvailableBlocks(moderators);
                for(Iterator<Block> it = moderators.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isModerator())it.remove();
                }
                int[] cellCount = new int[1];
                multiblock.forEachInternalPosition((x, y, z) -> {
                    Block b = multiblock.getBlock(x,y,z);
                    if(b!=null&&b.isFuelCell()){
                        cellCount[0]++;
                    }
                });
                suggestor.setCount((multiblock.getInternalVolume()-cellCount[0])*cells.size()*moderators.size());
                for(Block cell : cells){
                    for(Block moderator : moderators){
                        multiblock.forEachInternalPosition((x, y, z) -> {
                            Block was = multiblock.getBlock(x, y, z);
                            if(was!=null&&was.isFuelCell())return;
                            ArrayList<Action> actions = new ArrayList<>();
                            Block ce = (Block)cell.newInstance(x, y, z);
                            ce.fuel = cell.fuel;
                            actions.add(new SetblockAction(x, y, z, ce));
                            SetblocksAction multi = new SetblocksAction(moderator);
                            DIRECTION:for(Direction d : Direction.values()){
                                ArrayList<int[]> toSet = new ArrayList<>();
                                boolean yep = false;
                                for(int i = 1; i<=getSpecificConfiguration().settings.neutronReach+1; i++){
                                    int X = x+d.x*i;
                                    int Y = y+d.y*i;
                                    int Z = z+d.z*i;
                                    if(X==0||Y==0||Z==0||X==OverhaulSFR.this.x+1||Y==OverhaulSFR.this.y+1||Z==OverhaulSFR.this.z+1)break;//that's the casing
                                    if(!multiblock.contains(X, Y, Z))break;//end of the line
                                    Block b = multiblock.getBlock(X, Y, Z);
                                    if(b!=null){
                                        if(b.isModerator())continue;//already a moderator
                                        if(b.isFuelCell()){
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
                            if(suggestor.acceptingSuggestions())suggestor.suggest(new Suggestion("Add "+cell.getName()+(multi.isEmpty()?"":" with "+moderator.getName()), actions, priorities, cell.getTexture(), moderator.getTexture()));
                        });
                    }
                }
            }
        });
        suggestors.add(new Suggestor<OverhaulSFR>("Moderator Line Upgrader", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulSFR>("Cell Count", true, true){
                    @Override
                    protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                        return main.totalFuelCells-other.totalFuelCells;
                    }
                });
                priorities.add(new Priority<OverhaulSFR>("Efficiency", true, true){
                    @Override
                    protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                        return (int) Math.round(main.totalEfficiency*10000-other.totalEfficiency*10000);
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
                    if(!block.isModerator())it.remove();
                }
                int count = 0;
                for(Block block : multiblock.getBlocks()){
                    if(block.isFuelCell())count++;
                }
                suggestor.setCount(count*6*moderators.size());
                for(Block block : multiblock.getBlocks()){
                    if(!block.isFuelCell())continue;
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
                                if(b.isFuelCell()||b.isIrradiator()||b.isReflector())break;
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
        suggestors.add(new Suggestor<OverhaulSFR>("Single Moderator Upgrader", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulSFR>("Cell Count", true, true){
                    @Override
                    protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                        return main.totalFuelCells-other.totalFuelCells;
                    }
                });
                priorities.add(new Priority<OverhaulSFR>("Efficiency", true, true){
                    @Override
                    protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                        return (int) Math.round(main.totalEfficiency*10000-other.totalEfficiency*10000);
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
                ArrayList<Block> heatsinks = new ArrayList<>();
                multiblock.getAvailableBlocks(heatsinks);
                for(Iterator<Block> it = heatsinks.iterator(); it.hasNext();){
                    Block block = it.next();
                    if(!block.isHeatsink())it.remove();
                }
                int[] count = new int[1];
                multiblock.forEachInternalPosition((x, y, z) -> {
                    Block block = multiblock.getBlock(x, y, z);
                    if(block==null||block.canBeQuickReplaced()){
                        count[0]++;
                    }
                });
                suggestor.setCount(count[0]*heatsinks.size());
                multiblock.forEachInternalPosition((x, y, z) -> {
                    for(Block newBlock : heatsinks){
                        Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            int oldCooling = 0;
                            if(block!=null&&block.isHeatsinkActive())oldCooling = block.template.heatsink.cooling;
                            int newCooling = newBlock.template.heatsink.cooling;
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
        return b.isCasing()&&b.template.casing!=null&&b.template.casing.edge;
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
        Block inVent = null;
        Block outVent = null;
        HashMap<NCPFElement, BlockElement> inPorts = new HashMap<>();
        HashMap<NCPFElement, BlockElement> outPorts = new HashMap<>();
        for(BlockElement template : getSpecificConfiguration().blocks){
            if(template.casing!=null&&template.casing.edge)casing = new Block(getConfiguration(), 0, 0, 0, template);
            if(template.casing!=null&&!template.casing.edge&&template.controller==null&&template.coolantVent==null&&template.port==null&&template.neutronSource==null&&template.parent==null&&template.unToggled==null)window = new Block(getConfiguration(), 0, 0, 0, template);
            if(template.controller!=null)controller = new Block(getConfiguration(), 0, 0, 0, template);
            if(template.coolantVent!=null&&!template.coolantVent.output)inVent = new Block(getConfiguration(), 0, 0, 0, template);
            if(template.coolantVent!=null&&template.coolantVent.output)outVent = new Block(getConfiguration(), 0, 0, 0, template);
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
        final Block theInVent = inVent;
        final Block theOutVent = outVent;
        boolean[] hasPlacedTheController = new boolean[1];
        boolean[] hasPlacedTheVent = new boolean[2];
        HashSet<NCPFElement> hasPlacedTheInputPort = new HashSet<>();
        HashSet<NCPFElement> hasPlacedTheOutputPort = new HashSet<>();
        for(Block block : getBlocks()){
            if(block.template.controller!=null)hasPlacedTheController[0] = true;
            if(block.template.coolantVent!=null&&!block.template.coolantVent.output){
                hasPlacedTheVent[0] = true;
            }
            if(block.template.coolantVent!=null&&block.template.coolantVent.output){
                hasPlacedTheVent[1] = true;
            }
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
            if(!hasPlacedTheVent[0]){
                setBlock(x, y, z, theInVent);
                hasPlacedTheVent[0] = true;
                return;
            }
            if(!hasPlacedTheVent[1]){
                setBlock(x, y, z, theOutVent);
                hasPlacedTheVent[1] = true;
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
        return "multiblocks/overhaul_sfr";
    }
    @Override
    public LiteOverhaulSFR compile(){
        LiteOverhaulSFR sfr = new LiteOverhaulSFR(CompiledOverhaulSFRConfiguration.compile(getSpecificConfiguration()));
        sfr.importAndConvert(this);
        return sfr;
    }
    @Override
    public OverhaulSFRDesign convertToDesign(){
        OverhaulSFRDesign design = new OverhaulSFRDesign(Core.project, x, y, z);
        forEachPosition((x, y, z) -> {
            Block block = getBlock(x, y, z);
            design.design[x][y][z] = block==null?null:block.template;
            if(block!=null){
                design.fuels[x][y][z] = block.fuel;
                design.irradiatorRecipes[x][y][z] = block.irradiatorRecipe;
            }
        });
        design.coolantRecipe = coolantRecipe;
        return design;
    }
    @Override
    public NCPFElement[] getMultiblockRecipes(){
        return new NCPFElement[]{coolantRecipe};
    }
    @Override
    public void setMultiblockRecipe(int recipeType, NCPFElement recipe){
        coolantRecipe = (CoolantRecipe)recipe;
    }
}