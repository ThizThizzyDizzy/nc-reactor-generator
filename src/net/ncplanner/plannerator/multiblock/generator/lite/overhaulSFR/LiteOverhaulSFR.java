package net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.generator.lite.CompiledPlacementRule;
import net.ncplanner.plannerator.multiblock.generator.lite.GeneratorStage;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteGenerator;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.Priority;
import net.ncplanner.plannerator.multiblock.generator.lite.StageTransition;
import net.ncplanner.plannerator.multiblock.generator.lite.Symmetry;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.ConditionGreaterEqual;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.ConditionLess;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.ConditionLessEqual;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.RandomQuantityMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.SingleMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR.mutators.ClearInvalidMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR.mutators.random.RandomBlockMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableFloat;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorMaximum;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorMinimum;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorSubtraction;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingPercent;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe;
public class LiteOverhaulSFR extends LiteMultiblock<OverhaulSFR>{
    public final CompiledOverhaulSFRConfiguration configuration;
    public int[][][] sourceValid;
    public float[][][] sourceEfficiency;
    public int[][][] propogated;
    public int[][][] neutronFlux;
    public int[][][] hadFlux;
    public int[][][] moderatorLines;
    public float[][][] positionalEfficiency;
    public int[][][] blockActive;
    public int[][][] moderatorValid;
    public final int[] dims;
    public final int[][][] blocks;
    
    public float[][][] blockEfficiencyF;
    private final ArrayList<Cluster> clusters = new ArrayList<>();
    
    public int coolantRecipe;
    
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
    public int cells;
    
    private int[] blockCount;
    private int[][] heatsinkCalculationStepIndicies;
    public LiteOverhaulSFR(CompiledOverhaulSFRConfiguration configuration){
        this.configuration = configuration;
        blocks = new int[configuration.maxSize][configuration.maxSize][configuration.maxSize];
        blockCount = new int[configuration.blockDefinition.length];//only initialized this early for variables
        for(int x = 0; x<configuration.maxSize; x++){
            for(int y = 0; y<configuration.maxSize; y++){
                for(int z = 0; z<configuration.maxSize; z++){
                    blocks[x][y][z] = -1;
                }
            }
        }
        dims = new int[]{configuration.minSize,configuration.minSize,configuration.minSize};//default to minimum size
    }
    public void countBlocks(){
        blockCount = new int[configuration.blockDefinition.length];
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0)blockCount[blocks[x][y][z]]++;
                }
            }
        }
    }
    public void propogateNeutronFlux(int x, int y, int z, boolean force, boolean initial){
        if(!force&&sourceValid[x][y][z]==0&&neutronFlux[x][y][z]<configuration.blockCriticality[blocks[x][y][z]])return;
        if(!initial&&hadFlux[x][y][z]<configuration.blockCriticality[blocks[x][y][z]])return;
        if(propogated[x][y][z]>0)return;
        propogated[x][y][z]++;
        for(int[] d : directions){
            int flux = 0;
            int length = 0;
            float efficiency = 0;
            for(int i = 1; i<=configuration.neutronReach+1; i++){
                int X = x+d[0]*i;
                int Y = y+d[1]*i;
                int Z = z+d[2]*i;
                if(X<0||Y<0||Z<0||X>=dims[0]||Y>=dims[1]||Z>=dims[2])break;
                int block = blocks[X][Y][Z];
                if(block<0)break;
                if(configuration.blockModerator[block]){
                    flux+=configuration.blockFlux[block];
                    efficiency+=configuration.blockEfficiency[block];
                    length++;
                    continue;
                }
                if(configuration.blockShield[block]){
                    efficiency+=configuration.blockEfficiency[block];
                    length++;
                    continue;
                }
                if(configuration.blockFuelCell[block]){
                    if(length==0)break;
                    neutronFlux[X][Y][Z]+=flux;
                    moderatorLines[X][Y][Z]++;
                    if(flux>0)positionalEfficiency[X][Y][Z]+=efficiency/length;
                    propogateNeutronFlux(X, Y, Z, false, initial);
                    break;
                }
                if(configuration.blockReflector[block]){
                    if(length==0)break;
                    if(length>configuration.neutronReach/2)break;
                    neutronFlux[x][y][z]+=flux*2*configuration.blockReflectivity[block];
                    if(flux>0)positionalEfficiency[x][y][z]+=efficiency/length*configuration.blockEfficiency[block];
                    moderatorLines[x][y][z]++;
                    break;
                }
                if(configuration.blockIrradiator[block]){
                    if(length==0)break;
                    moderatorLines[x][y][z]++;
                    if(flux>0)positionalEfficiency[x][y][z]+=efficiency/length*configuration.blockEfficiency[block];
                    break;
                }
                break;
            }
        }
    }
    public void postFluxCalc(int x, int y, int z){
        if(neutronFlux[x][y][z]<configuration.blockCriticality[blocks[x][y][z]])return;
        blockActive[x][y][z] = 1;
        for(int[] d : directions){
            int flux = 0;
            int length = 0;
            int[] shieldFluxes = new int[configuration.neutronReach+2];
            int[] toActivate = new int[configuration.neutronReach+2];
            int[] toValidate = new int[configuration.neutronReach+2];
            for(int i = 1; i<=configuration.neutronReach+1; i++){
                int X = x+d[0]*i;
                int Y = y+d[1]*i;
                int Z = z+d[2]*i;
                if(X<0||Y<0||Z<0||X>=dims[0]||Y>=dims[1]||Z>=dims[2])break;
                int block = blocks[X][Y][Z];
                if(block<0)break;
                boolean skip = false;
                if(configuration.blockModerator[block]){
                    length++;
                    flux+=configuration.blockFlux[block];
                    if(i==1)toActivate[i]++;
                    toValidate[i]++;
                    skip = true;
                }
                if(configuration.blockShield[block]){
                    length++;
                    if(i==1)toActivate[i]++;
                    toValidate[i]++;
                    blockActive[X][Y][Z]++;
                    shieldFluxes[i] = flux;
                    skip = true;
                }
                if(skip)continue;
                if(configuration.blockFuelCell[block]&&neutronFlux[X][Y][Z]>=configuration.blockCriticality[block]){
                    if(length==0)break;
                    for(int j = 0; j<shieldFluxes.length; j++){
                        int bx = x+d[0]*j;
                        int by = y+d[1]*j;
                        int bz = z+d[2]*j;
                        if(bx<0||by<0||bz<0||bx>=dims[0]||by>=dims[1]||bz>=dims[2])break;
                        neutronFlux[bx][by][bz]+=shieldFluxes[j];
                        blockActive[bx][by][bz]+=toActivate[j];
                        moderatorValid[bx][by][bz]+=toValidate[j];
                    }
                    break;
                }
                if(configuration.blockReflector[block]){
                    if(length==0)break;
                    if(length>configuration.neutronReach/2)break;
                    blockActive[X][Y][Z]++;
                    for(int j = 0; j<shieldFluxes.length; j++){
                        int bx = x+d[0]*j;
                        int by = y+d[1]*j;
                        int bz = z+d[2]*j;
                        if(bx<0||by<0||bz<0||bx>=dims[0]||by>=dims[1]||bz>=dims[2])break;
                        neutronFlux[bx][by][bz]+=flux*(1+configuration.blockReflectivity[block]);
                        blockActive[bx][by][bz]+=toActivate[j];
                        moderatorValid[bx][by][bz]+=toValidate[j];
                    }
                    break;
                }
                if(configuration.blockIrradiator[block]){
                    if(length==0)break;
                    neutronFlux[X][Y][Z]+=flux;
                    blockActive[X][Y][Z]++;
                    for(int j = 0; j<shieldFluxes.length; j++){
                        int bx = x+d[0]*j;
                        int by = y+d[1]*j;
                        int bz = z+d[2]*j;
                        if(bx<0||by<0||bz<0||bx>=dims[0]||by>=dims[1]||bz>=dims[2])break;
                        neutronFlux[bx][by][bz]+=shieldFluxes[j];
                        blockActive[bx][by][bz]+=toActivate[j];
                        moderatorValid[bx][by][bz]+=toValidate[j];
                    }
                    break;
                }
            }
        }
        propogated[x][y][z]++;
    }
    public void optimizeHeatsinkSteps(){
        int steps = 0;
        int[][] newCCSIs = new int[configuration.heatsinkCalculationStepIndicies.length][];
        for(int[] indicies : configuration.heatsinkCalculationStepIndicies){
            int stps = 0;
            int[] newIndicies = new int[indicies.length];
            for(int index : indicies){
                if(blockCount[index]>0){
                    newIndicies[stps] = index;
                    stps++;
                }
            }
            int[] newNewIndicies = new int[stps];
            for(int i = 0; i<stps; i++){
                newNewIndicies[i] = newIndicies[i];
            }
            newCCSIs[steps] = newNewIndicies;
            steps++;
        }
        heatsinkCalculationStepIndicies = new int[steps][];
        System.arraycopy(newCCSIs, 0, heatsinkCalculationStepIndicies, 0, steps);
    }
    public void calculateHeatsinks(){
        int[] adjacents = new int[]{-2,-2,-2,-2,-2,-2};
        int[] active = new int[6];
        int somethingChanged;
        do{
            somethingChanged = 0;
            for(int[] indicies : heatsinkCalculationStepIndicies){
                for(int x = 0; x<dims[0]; x++){
                    for(int y = 0; y<dims[1]; y++){
                        for(int z = 0; z<dims[2]; z++){
                            B:for(int c : indicies){
                                if(blocks[x][y][z]==c){
                                    if(x>0){
                                        adjacents[0] = blocks[x-1][y][z];
                                        active[0] = blockActive[x-1][y][z];
                                    }else{
                                        adjacents[0] = -2;
                                        active[0] = 0;
                                    }
                                    if(y>0){
                                        adjacents[1] = blocks[x][y-1][z];
                                        active[1] = blockActive[x][y-1][z];
                                    }else{
                                        adjacents[1] = -2;
                                        active[1] = 0;
                                    }
                                    if(z>0){
                                        adjacents[2] = blocks[x][y][z-1];
                                        active[2] = blockActive[x][y][z-1];
                                    }else{
                                        adjacents[2] = -2;
                                        active[2] = 0;
                                    }
                                    if(x<dims[0]-1){
                                        adjacents[3] = blocks[x+1][y][z];
                                        active[3] = blockActive[x+1][y][z];
                                    }else{
                                        adjacents[3] = -2;
                                        active[3] = 0;
                                    }
                                    if(y<dims[1]-1){
                                        adjacents[4] = blocks[x][y+1][z];
                                        active[4] = blockActive[x][y+1][z];
                                    }else{
                                        adjacents[4] = -2;
                                        active[4] = 0;
                                    }
                                    if(z<dims[2]-1){
                                        adjacents[5] = blocks[x][y][z+1];
                                        active[5] = blockActive[x][y][z+1];
                                    }else{
                                        adjacents[5] = -2;
                                        active[5] = 0;
                                    }
                                    int was = blockActive[x][y][z];
                                    blockActive[x][y][z] = 1;
                                    for(CompiledPlacementRule rule : configuration.blockPlacementRules[c]){
                                        if(!rule.isValid(adjacents, active, configuration.blockType)){
                                            blockActive[x][y][z] = 0;
                                            somethingChanged += was-blockActive[x][y][z];
                                            break B;
                                        }
                                    }
                                    somethingChanged += blockActive[x][y][z]-was;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }while(somethingChanged>0&&configuration.hasRecursiveRules);
    }
    public void initCells(){
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    int block = blocks[x][y][z];
                    if(block>=0&&configuration.blockFuelCell[block]){
                        cells+=blockActive[x][y][z]>0?1:0;
                        float criticalityModifier = (float) (1/(1+MathUtil.exp(2*(neutronFlux[x][y][z]-2*configuration.blockCriticality[block]))));
                        blockEfficiencyF[x][y][z] = configuration.blockEfficiency[block]*positionalEfficiency[x][y][z]*(sourceValid[x][y][z]>0?sourceEfficiency[x][y][z]:1)*criticalityModifier;
                    }
                }
            }
        }
    }
    public void initConductors(){
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    int block = blocks[x][y][z];
                    if(block>=0&&configuration.blockConductor[block]){
                        blockActive[x][y][z] = 1;
                    }
                }
            }
        }
    }
    public void buildClusters(){
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    Cluster cluster = getCluster(x, y, z);
                    if(cluster==null)continue;//that's not a cluster!
                    if(clusters.contains(cluster))continue;//already know about that one!
                    clusters.add(cluster);
                }
            }
        }
    }
    public void calculateClusters(){
        for(int i = 0; i<clusters.size(); i++){
            Cluster cluster = clusters.get(i);
            int fuelCells = 0;
            for(int j = 0; j<cluster.blocks.size(); j++){
                BlockPos b = cluster.blocks.get(j);
                int x = b.x;
                int y = b.y;
                int z = b.z;
                int block = blocks[b.x][b.y][b.z];
                if(block==-1)continue;
                if(configuration.blockFuelCell[block]&&blockActive[x][y][z]>0){
                    fuelCells++;
                    cluster.totalOutput+=configuration.blockHeat[block]*blockEfficiencyF[x][y][z];
                    cluster.efficiency+=blockEfficiencyF[x][y][z];
                    cluster.totalHeat+=moderatorLines[x][y][z]*configuration.blockHeat[block];
                    cluster.heatMult+=moderatorLines[x][y][z];
                }
                if(configuration.blockHeatsink[block]&&blockActive[x][y][z]>0){
                    cluster.totalCooling+=configuration.blockCooling[block];
                }
                if(configuration.blockShield[block]&&blockActive[x][y][z]>0){
                    cluster.totalOutput+=configuration.blockHeatPerFlux[block]*neutronFlux[x][y][z]*configuration.blockEfficiency[block];
                    cluster.totalHeat+=configuration.blockHeatPerFlux[block]*neutronFlux[x][y][z];
                }
                if(configuration.blockIrradiator[block]&&blockActive[x][y][z]>0){
                    cluster.irradiation+=neutronFlux[x][y][z];
                    cluster.totalHeat+=configuration.blockHeat[block]*neutronFlux[x][y][z];
                }
            }
            cluster.efficiency/=fuelCells;
            cluster.heatMult/=fuelCells;
            if(Double.isNaN(cluster.efficiency))cluster.efficiency = 0;
            if(Double.isNaN(cluster.heatMult))cluster.heatMult = 0;
            cluster.netHeat = cluster.totalHeat-cluster.totalCooling;
            if(cluster.totalCooling==0)cluster.coolingPenaltyMult = 1;
            else cluster.coolingPenaltyMult = Math.min(1, (cluster.totalHeat+configuration.coolingEfficiencyLeniency)/(float)cluster.totalCooling);
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
        }
    }
    public void calculateStats(){
        totalEfficiency/=totalFuelCells;
        totalHeatMult/=totalFuelCells;
        if(Double.isNaN(totalEfficiency))totalEfficiency = 0;
        if(Double.isNaN(totalHeatMult))totalHeatMult = 0;
        functionalBlocks = 0;
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    //TODO exclude non-clustered blocks
                    int block = blocks[x][y][z];
                    if(block<0)continue;
                    if(blockActive[x][y][z]+moderatorValid[x][y][z]>0){
                        if(configuration.blockFuelCell[block]||configuration.blockModerator[block]||configuration.blockReflector[block]||configuration.blockIrradiator[block]||configuration.blockHeatsink[block]||configuration.blockShield[block]){
                            functionalBlocks++;
                        }
                    }
                }
            }
        }
        int volume = dims[0]*dims[1]*dims[2];
        sparsityMult = (float) (functionalBlocks/(float)volume>=configuration.sparsityPenaltyThreshold?1:configuration.sparsityPenaltyMultiplier+(1-configuration.sparsityPenaltyMultiplier)*Math.sin(Math.PI*functionalBlocks/(2*volume*configuration.sparsityPenaltyThreshold)));
        totalOutput*=sparsityMult;
        totalEfficiency*=sparsityMult;
        totalOutput/=configuration.coolantRecipeHeat[coolantRecipe]/configuration.coolantRecipeOutputRatio[coolantRecipe];
    }
    @Override
    public void calculate(){
        clusters.clear();
        sourceValid = new int[dims[0]][dims[1]][dims[2]];
        sourceEfficiency = new float[dims[0]][dims[1]][dims[2]];
        propogated = new int[dims[0]][dims[1]][dims[2]];
        neutronFlux = new int[dims[0]][dims[1]][dims[2]];
        hadFlux = new int[dims[0]][dims[1]][dims[2]];
        moderatorLines = new int[dims[0]][dims[1]][dims[2]];
        positionalEfficiency = new float[dims[0]][dims[1]][dims[2]];
        moderatorValid = new int[dims[0]][dims[1]][dims[2]];
        blockActive = new int[dims[0]][dims[1]][dims[2]];
        blockEfficiencyF = new float[dims[0]][dims[1]][dims[2]];
        
        totalFuelCells = 0;
        rawOutput = 0;
        totalOutput = 0;
        totalCooling = 0;
        totalHeat = 0;
        netHeat = 0;
        cells = 0;
        totalEfficiency = 0;
        totalHeatMult = 0;
        totalIrradiation = 0;
        functionalBlocks = 0;
        sparsityMult = 0;
        shutdownFactor = 0;
        
        countBlocks();
        //<editor-fold defaultstate="collapsed" desc="Base flux propogation">
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                        sourceValid[x][y][z]+=findSources(x, y, z, configuration.losTest);
                        sourceEfficiency[x][y][z] = sourceValid[x][y][z]>0?1:0;//TODO let you choose neutron sources
                    }
                }
            }
        }
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]&&sourceValid[x][y][z]>0){
                        propogateNeutronFlux(x, y, z, false, true);
                    }
                }
            }
        }
        //repropogate as many times as neccesary
        int lastActive, nowActive;
        do{
            lastActive = 0;
            for(int x = 0; x<dims[0]; x++){
                for(int y = 0; y<dims[1]; y++){
                    for(int z = 0; z<dims[2]; z++){
                        hadFlux[x][y][z] = neutronFlux[x][y][z];
                        if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                            if(neutronFlux[x][y][z]>=configuration.blockCriticality[blocks[x][y][z]])lastActive++;
                            propogateNeutronFlux(x, y, z, false, false);
                        }
                    }
                }
            }
            //clear everything for repropogation
            propogated = new int[dims[0]][dims[1]][dims[2]];
            neutronFlux = new int[dims[0]][dims[1]][dims[2]];
            moderatorLines = new int[dims[0]][dims[1]][dims[2]];
            positionalEfficiency = new float[dims[0]][dims[1]][dims[2]];
            
            nowActive = 0;
            for(int x = 0; x<dims[0]; x++){
                for(int y = 0; y<dims[1]; y++){
                    for(int z = 0; z<dims[2]; z++){
                        if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                            propogateNeutronFlux(x, y, z, false, false);
                        }
                    }
                }
            }
            
            for(int x = 0; x<dims[0]; x++){
                for(int y = 0; y<dims[1]; y++){
                    for(int z = 0; z<dims[2]; z++){
                        if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                            if(neutronFlux[x][y][z]>=configuration.blockCriticality[blocks[x][y][z]])nowActive++;
                            if(hadFlux[x][y][z]<configuration.blockCriticality[blocks[x][y][z]])neutronFlux[x][y][z] = hadFlux[x][y][z];
                        }
                    }
                }
            }
        }while(nowActive!=lastActive);
        for(int x = 0; x<dims[0]; x++){ //post flux calc
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                        postFluxCalc(x, y, z);
                    }
                }
            }
        }
//</editor-fold>
        //don't care about partial
        //<editor-fold defaultstate="collapsed" desc="Shutdown flux propogation (DISABLED)">
        /*
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]&&sourceValid[x][y][z]>0){
                        propogateNeutronFlux(x, y, z, blockActive[x][y][z]>0, true);
                    }
                }
            }
        }
        //repropogate as many times as neccesary
        do{
            lastActive = 0;
            for(int x = 0; x<dims[0]; x++){
                for(int y = 0; y<dims[1]; y++){
                    for(int z = 0; z<dims[2]; z++){
                        hadFlux[x][y][z] = neutronFlux[x][y][z];
                        if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                            if(neutronFlux[x][y][z]>=configuration.blockCriticality[blocks[x][y][z]])lastActive++;
                            propogateNeutronFlux(x, y, z, false, false);
                        }
                    }
                }
            }
            //clear everything for repropogation
            propogated = new int[dims[0]][dims[1]][dims[2]];
            neutronFlux = new int[dims[0]][dims[1]][dims[2]];
            moderatorLines = new int[dims[0]][dims[1]][dims[2]];
            positionalEfficiency = new float[dims[0]][dims[1]][dims[2]];
            
            nowActive = 0;
            for(int x = 0; x<dims[0]; x++){
                for(int y = 0; y<dims[1]; y++){
                    for(int z = 0; z<dims[2]; z++){
                        if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                            propogateNeutronFlux(x, y, z, false, false);
                        }
                    }
                }
            }
            
            for(int x = 0; x<dims[0]; x++){
                for(int y = 0; y<dims[1]; y++){
                    for(int z = 0; z<dims[2]; z++){
                        if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                            if(neutronFlux[x][y][z]>=configuration.blockCriticality[blocks[x][y][z]])nowActive++;
                            if(hadFlux[x][y][z]<configuration.blockCriticality[blocks[x][y][z]])neutronFlux[x][y][z] = hadFlux[x][y][z];
                        }
                    }
                }
            }
        }while(nowActive!=lastActive);
        for(int x = 0; x<dims[0]; x++){ //post flux calc
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                        postFluxCalc(x, y, z);
                    }
                }
            }
        }
        */
//</editor-fold>
        optimizeHeatsinkSteps();
        calculateHeatsinks();
        initCells();
        initConductors();
        buildClusters();
        calculateClusters();
        calculateStats();
        //also shutdown factor or whatever
    }
    //adjacents
    private int findSources(int x, int y, int z, boolean[] pathTest){
        int count = 0;
        for(int[] direction : directions){
            count+=findSource(x, y, z, direction[0], direction[1], direction[2], pathTest);
        }
        return count;
    }
    private int findSource(int x, int y, int z, int dx, int dy, int dz, boolean[] pathTest){
        for(int dist = 0; dist<=configuration.maxSize; dist++){
            x+=dx;
            y+=dy;
            z+=dz;
            if(x<0||y<0||z<0||x>=dims[0]||y>=dims[1]||z>=dims[2])return 1;
            if(blocks[x][y][z]==-1)continue;
            if(!pathTest[blocks[x][y][z]])return 0;
        }
        return 0;
    }
    
    @Override
    public void importAndConvert(OverhaulSFR sfr){
        dims[0] = sfr.getInternalWidth();
        dims[1] = sfr.getInternalHeight();
        dims[2] = sfr.getInternalDepth();
        sfr.forEachInternalPosition((x, y, z) -> {
            Block block = sfr.getBlock(x, y, z);
            NCPFElementDefinition definition = block==null?null:block.template.definition;
            NCPFElementDefinition recipe = block==null?null:(block.fuel==null?(block.irradiatorRecipe==null?null:block.irradiatorRecipe.definition):block.fuel.definition);
            int b = -1;
            for(int i = 0; i<configuration.blockDefinition.length; i++){
                if(configuration.blockDefinition[i].matches(definition)){
                    if(recipe==null||configuration.blockRecipe[i].matches(recipe))b = i;
                }
            }
            blocks[x-1][y-1][z-1] = b;
        });
        int r = 0;
        for(int i = 0; i<configuration.coolantRecipeDefinition.length; i++){
            if(configuration.coolantRecipeDefinition[i].matches(sfr.coolantRecipe.definition))r = i;
        }
        coolantRecipe = r;
    }
    //tooltip
    //variables
    @Override
    public LiteOverhaulSFR copy(){
        LiteOverhaulSFR copy = new LiteOverhaulSFR(configuration);
        copy.copyFrom(this);
        return copy;
    }
    @Override
    public void copyFrom(LiteMultiblock<OverhaulSFR> other){
        LiteOverhaulSFR sfr = (LiteOverhaulSFR)other;
        coolantRecipe = sfr.coolantRecipe;
        dims[0] = sfr.dims[0];;
        dims[1] = sfr.dims[1];
        dims[2] = sfr.dims[2];
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                System.arraycopy(sfr.blocks[x][y], 0, blocks[x][y], 0, dims[2]);
            }
        }
    }
    //copyVarsFrom
    @Override
    public OverhaulSFR export(NCPFConfigurationContainer configg){
        OverhaulSFRConfiguration config = configg.getConfiguration(OverhaulSFRConfiguration::new);
        CoolantRecipe coolantRecipe = null;
        for(CoolantRecipe r : config.coolantRecipes){
            if(r.definition.matches(configuration.coolantRecipeDefinition[this.coolantRecipe])){
                coolantRecipe = r;
                break;
            }
        }
        calculate();
        OverhaulSFR sfr = new OverhaulSFR(configg, dims[0], dims[1], dims[2], coolantRecipe);
        sfr.forEachInternalPosition((x, y, z) -> {
            int block = blocks[x-1][y-1][z-1];
            if(moderatorValid[x-1][y-1][z-1]+blockActive[x-1][y-1][z-1]<=0)block = -1;
            Block bl = null;
            if(block>=0){
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement b : config.blocks){
                    if(b.definition.matches(configuration.blockDefinition[block])){
                        bl = new Block(configg, x, y, z, b);
                        NCPFElementDefinition recip = configuration.blockRecipe[block];
                        if(recip!=null){
                            for(Fuel fuel : b.fuels){
                                if(fuel.definition.matches(recip))bl.fuel = fuel;
                            }
                            for(IrradiatorRecipe recipe : b.irradiatorRecipes){
                                if(recipe.definition.matches(recip))bl.irradiatorRecipe = recipe;
                            }
                        }
                        break;
                    }
                }
            }
            sfr.setBlock(x, y, z, bl);
        });
        if(Core.autoBuildCasing)sfr.buildDefaultCasing();
        return sfr;
    }
    @Override
    public int getDimension(int id){
        return dims[id];
    }
    @Override
    public Image getBlockTexture(int x, int y, int z){
        if(moderatorValid==null||blockActive==null)return null;
        if(moderatorValid[x][y][z]+blockActive[x][y][z]<1)return null;
        int block = blocks[x][y][z];
        return block>=0?configuration.blockTexture[block]:null;
    }
    @Override
    public float getCubeBounds(int x, int y, int z, int index){
        if(index<3)return 0;
        return 1;
    }
    //generators
    @Override
    public void clear(){
        for(int x = 0; x<configuration.maxSize; x++){
            for(int y = 0; y<configuration.maxSize; y++){
                for(int z = 0; z<configuration.maxSize; z++){
                    blocks[x][y][z] = -1;
                }
            }
        }
        calculate();//fix vars
    }
    private Cluster getCluster(int x, int y, int z){
        int block = blocks[x][y][z];
        if(block<0)return null;
        if(!canCluster(x, y, z))return null;
        synchronized(clusters){
            for(Cluster cluster : clusters){
                if(cluster.contains(x, y, z))return cluster;
            }
        }
        return new Cluster(x, y, z);
    }
    private boolean canCluster(int x, int y, int z){
        int block = blocks[x][y][z];
        if(blockActive[x][y][z]<=0)return configuration.blockConductor[block];
        return configuration.blockConductor[block]||configuration.blockFuelCell[block]||configuration.blockIrradiator[block]||configuration.blockHeatsink[block]||configuration.blockShield[block];
    }
    private boolean contains(int x, int y, int z){
        return x>=0&&y>=0&&z>=0&&x<dims[0]&&y<dims[1]&&z<dims[2];
    }
    @Override
    public String getTooltip(){
        int validClusters = 0;
        for(int i = 0; i<clusters.size(); i++){
            if(clusters.get(i).isValid())validClusters++;
        }
        return "Total output: "+totalOutput+" mb/t of whatever "+configuration.coolantRecipeDisplayName[coolantRecipe]+" gets turned into\n"
                + "Total Heat: "+totalHeat+"H/t\n"
                + "Total Cooling: "+totalCooling+"H/t\n"
                + "Net Heat: "+netHeat+"H/t\n"
                + "Overall Efficiency: "+MathUtil.percent(totalEfficiency, 0)+"\n"
                + "Overall Heat Multiplier: "+MathUtil.percent(totalHeatMult, 0)+"\n"
                + "Sparsity Penalty Multiplier: "+Math.round(sparsityMult*10000)/10000d+"\n"
                + "Clusters: "+(validClusters==clusters.size()?clusters.size():(validClusters+"/"+clusters.size()))+"\n"
                + "Total Irradiation: "+totalIrradiation+"\n"
                + "Shutdown Factor: "+MathUtil.percent(shutdownFactor, 2);
    }
    @Override
    public void copyVarsFrom(LiteMultiblock<OverhaulSFR> other){
        LiteOverhaulSFR sfr = (LiteOverhaulSFR) other;
        netHeat = sfr.netHeat;
        totalOutput = sfr.totalOutput;
        totalHeat = sfr.totalHeat;
        totalCooling = sfr.totalCooling;
        cells = sfr.cells;
        totalEfficiency = sfr.totalEfficiency;
        totalHeatMult = sfr.totalHeatMult;
        if(moderatorValid==null)moderatorValid = new int[dims[0]][dims[1]][dims[2]];
        if(blockActive==null)blockActive = new int[dims[0]][dims[1]][dims[2]];
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                System.arraycopy(sfr.moderatorValid[x][y], 0, moderatorValid[x][y], 0, dims[2]);
                System.arraycopy(sfr.blockActive[x][y], 0, blockActive[x][y], 0, dims[2]);
            }
        }
        System.arraycopy(sfr.blockCount, 0, blockCount, 0, blockCount.length);
    }
    @Override
    public Variable[] genVariables(){
        Variable[] vars = new Variable[7+blockCount.length];
        vars[0] = new VariableInt("Net Heat"){
            @Override
            public int getValue(){
                return netHeat;
            }
        };
        vars[1] = new VariableFloat("Total Output"){
            @Override
            public float getValue(){
                return totalOutput;
            }
        };
        vars[2] = new VariableInt("Total Heat"){
            @Override
            public int getValue(){
                return totalHeat;
            }
        };
        vars[3] = new VariableInt("Total Cooling"){
            @Override
            public int getValue(){
                return totalCooling;
            }
        };
        vars[4] = new VariableInt("Cell Count"){
            @Override
            public int getValue(){
                return cells;
            }
        };
        vars[5] = new VariableFloat("Total Efficiency"){
            @Override
            public float getValue(){
                return totalEfficiency;
            }
        };
        vars[6] = new VariableFloat("Heat Multiplier"){
            @Override
            public float getValue(){
                return totalHeatMult;
            }
        };
        for(int i = 0; i<blockCount.length; i++){
            int j = i;
            vars[7+i] = new VariableInt("Block Count: "+configuration.blockDefinition[i]){
                @Override
                public int getValue(){
                    return blockCount[j];
                }
            };
        }
        return vars;
    }
    private class Cluster{
        public ArrayList<BlockPos> blocks = new ArrayList<>();
        public boolean isConnectedToWall = false;
        public float totalOutput = 0;
        public float efficiency;
        public int totalHeat, totalCooling, netHeat;
        public float heatMult, coolingPenaltyMult;
        public int irradiation;
        public Cluster(int x, int y, int z){
            blocks.addAll(toList(getClusterBlocks(new BlockPos(x, y, z))));
            isConnectedToWall = wallCheck(blocks);
            if(!isConnectedToWall){
                isConnectedToWall = wallCheck(toList(getClusterBlocks(new BlockPos(x, y, z))));
            }
        }
        private Cluster(){}
        private boolean isValid(){
            return isConnectedToWall&&isCreated();
        }
        public boolean isCreated(){
            for(BlockPos pos : blocks){
                int block = LiteOverhaulSFR.this.blocks[pos.x][pos.y][pos.z];
                if(block<0)continue;
                if(configuration.blockFuelCell[block]||configuration.blockIrradiator[block]||configuration.blockShield[block])return true;
            }
            return false;
        }
        public boolean contains(Block block){
            return blocks.contains(block);
        }
        public boolean contains(int x, int y, int z){
            for(BlockPos b : blocks){
                if(b.x==x&&b.y==y&&b.z==z)return true;
            }
            return false;
        }
        private boolean wallCheck(ArrayList<BlockPos> blocks){
            for(BlockPos pos : blocks){
                if(pos.x==1||pos.y==1||pos.z==1)return true;
                if(pos.x==dims[0]-1||pos.y==dims[1]-1||pos.z==dims[2]-1)return true;
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
    }
    /**
     * Block search algorithm from my Tree Feller for Bukkit.
     */
    private HashMap<Integer, ArrayList<BlockPos>> getClusterBlocks(BlockPos start){
        //layer zero
        HashMap<Integer, ArrayList<BlockPos>>results = new HashMap<>();
        ArrayList<BlockPos> zero = new ArrayList<>();
        if(canCluster(start.x, start.y, start.z)){
            zero.add(start);
        }
        results.put(0, zero);
        //all the other layers
        int maxDistance = dims[0]*dims[1]*dims[2];//the algorithm requires a max search distance. Rather than changing that, I'll just be lazy and give it a big enough number
        for(int i = 0; i<maxDistance; i++){
            ArrayList<BlockPos> layer = new ArrayList<>();
            ArrayList<BlockPos> lastLayer = new ArrayList<>(results.get(i));
            if(i==0&&lastLayer.isEmpty()){
                lastLayer.add(start);
            }
            for(BlockPos block : lastLayer){
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
                    BlockPos newBlock = new BlockPos(block.x+dx,block.y+dy,block.z+dz);
                    if(blocks[newBlock.x][newBlock.y][newBlock.z]<0)continue;
                    if(!(canCluster(newBlock.x, newBlock.y, newBlock.z))){//that's not part of this bunch
                        continue;
                    }
                    for(BlockPos oldbl : lastLayer){//if(lastLayer.contains(newBlock))continue;//if the new block is on the same layer, ignore
                        if(oldbl.equals(newBlock)){
                            continue FOR;
                        }
                    }
                    if(i>0){
                        for(BlockPos oldbl : results.get(i-1)){//if(i>0&&results.get(i-1).contains(newBlock))continue;//if the new block is on the previous layer, ignore
                            if(oldbl.equals(newBlock)){
                                continue FOR;
                            }
                        }
                    }
                    for(BlockPos oldbl : layer){//if(layer.contains(newBlock))continue;//if the new block is on the next layer, but already processed, ignore
                        if(oldbl.equals(newBlock)){
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
    private static ArrayList<BlockPos> toList(HashMap<Integer, ArrayList<BlockPos>> blocks){
        ArrayList<BlockPos> list = new ArrayList<>();
        for(int i : blocks.keySet()){
            list.addAll(blocks.get(i));
        }
        return list;
    }
}