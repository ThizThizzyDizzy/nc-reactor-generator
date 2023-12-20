package net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.image.Image;
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
import net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR.mutators.ClearInvalidMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR.mutators.random.RandomBlockMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableFloat;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorMaximum;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorMinimum;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorSubtraction;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingPercent;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.ActiveCoolerRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel;
public class LiteUnderhaulSFR implements LiteMultiblock<UnderhaulSFR>{
    public final CompiledUnderhaulSFRConfiguration configuration;
    public int[][][] cellEfficiency;
    public int[][][] blockEfficiency;
    public int[][][] blockValid;//not comprehensive, add blockEfficiency to be sure
    public final int[] dims;
    public final int[][][] blocks;
    
    public int fuel;
    
    public int netHeat;
    private int power, heat, cooling, cells;
    private float powerf, heatf, efficiency, heatMult;
    private int[] blockCount;
    private int[][] coolerCalculationStepIndicies;
    private Variable[] vars;
    public LiteUnderhaulSFR(CompiledUnderhaulSFRConfiguration configuration){
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
    public void calculateCells(){
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                        blockEfficiency[x][y][z] = cellEfficiency[x][y][z] = countAdjacents(x, y, z, configuration.blockFuelCell, configuration.blockModerator, configuration.neutronReach)+1;
                        powerf+=cellEfficiency[x][y][z]*configuration.fuelPower[fuel];
                        heatf+=(cellEfficiency[x][y][z]*(cellEfficiency[x][y][z]+1))/2f*configuration.fuelHeat[fuel];
                        cells+=blockEfficiency[x][y][z]>0?1:0;
                    }
                }
            }
        }
    }
    public void calculateModerators(){
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockModerator[blocks[x][y][z]]){
                        if(x>0)blockEfficiency[x][y][z]+=cellEfficiency[x-1][y][z];
                        if(y>0)blockEfficiency[x][y][z]+=cellEfficiency[x][y-1][z];
                        if(z>0)blockEfficiency[x][y][z]+=cellEfficiency[x][y][z-1];
                        if(x<dims[0]-1)blockEfficiency[x][y][z]+=cellEfficiency[x+1][y][z];
                        if(y<dims[1]-1)blockEfficiency[x][y][z]+=cellEfficiency[x][y+1][z];
                        if(z<dims[2]-1)blockEfficiency[x][y][z]+=cellEfficiency[x][y][z+1];
                        powerf+=blockEfficiency[x][y][z]*configuration.fuelPower[fuel]*configuration.moderatorExtraPower/6;
                        heatf+=blockEfficiency[x][y][z]*configuration.fuelHeat[fuel]*configuration.moderatorExtraHeat/6;
                    }
                }
            }
        }
    }
    public void optimizeCoolerSteps(){
        int steps = 0;
        int[][] newCCSIs = new int[configuration.coolerCalculationStepIndicies.length][];
        for(int[] indicies : configuration.coolerCalculationStepIndicies){
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
        coolerCalculationStepIndicies = new int[steps][];
        System.arraycopy(newCCSIs, 0, coolerCalculationStepIndicies, 0, steps);
    }
    public void calculateCoolers(){
        int[] adjacents = new int[]{-2,-2,-2,-2,-2,-2};
        int[] active = new int[6];
        int somethingChanged;
        do{
            somethingChanged = 0;
            for(int[] indicies : coolerCalculationStepIndicies){
                for(int x = 0; x<dims[0]; x++){
                    for(int y = 0; y<dims[1]; y++){
                        for(int z = 0; z<dims[2]; z++){
                            B:for(int c : indicies){
                                if(blocks[x][y][z]==c){
                                    if(x>0){
                                        adjacents[0] = blocks[x-1][y][z];
                                        active[0] = blockEfficiency[x-1][y][z];
                                    }else{
                                        adjacents[0] = -2;
                                        active[0] = 0;
                                    }
                                    if(y>0){
                                        adjacents[1] = blocks[x][y-1][z];
                                        active[1] = blockEfficiency[x][y-1][z];
                                    }else{
                                        adjacents[1] = -2;
                                        active[1] = 0;
                                    }
                                    if(z>0){
                                        adjacents[2] = blocks[x][y][z-1];
                                        active[2] = blockEfficiency[x][y][z-1];
                                    }else{
                                        adjacents[2] = -2;
                                        active[2] = 0;
                                    }
                                    if(x<dims[0]-1){
                                        adjacents[3] = blocks[x+1][y][z];
                                        active[3] = blockEfficiency[x+1][y][z];
                                    }else{
                                        adjacents[3] = -2;
                                        active[3] = 0;
                                    }
                                    if(y<dims[1]-1){
                                        adjacents[4] = blocks[x][y+1][z];
                                        active[4] = blockEfficiency[x][y+1][z];
                                    }else{
                                        adjacents[4] = -2;
                                        active[4] = 0;
                                    }
                                    if(z<dims[2]-1){
                                        adjacents[5] = blocks[x][y][z+1];
                                        active[5] = blockEfficiency[x][y][z+1];
                                    }else{
                                        adjacents[5] = -2;
                                        active[5] = 0;
                                    }
                                    int was = blockEfficiency[x][y][z];
                                    blockEfficiency[x][y][z] = 1;
                                    for(CompiledPlacementRule rule : configuration.blockPlacementRules[c]){
                                        if(!rule.isValid(adjacents, active, configuration.blockType)){
                                            blockEfficiency[x][y][z] = 0;
                                            cooling-=configuration.blockCooling[c]*(was-blockEfficiency[x][y][z]);
                                            somethingChanged += was-blockEfficiency[x][y][z];
                                            break B;
                                        }
                                    }
                                    cooling+=configuration.blockCooling[c]*(blockEfficiency[x][y][z]-was);
                                    somethingChanged += blockEfficiency[x][y][z]-was;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }while(somethingChanged>0&&configuration.hasRecursiveRules);
    }
    @Override
    public void calculate(){
        //reset stats
        efficiency = heatMult = powerf = heatf = power = heat = netHeat = cooling = cells = 0;
        blockEfficiency = new int[dims[0]][dims[1]][dims[2]];//probably faster than clearing it manually
        blockValid = new int[dims[0]][dims[1]][dims[2]];//probably faster than clearing it manually
        cellEfficiency = new int[dims[0]][dims[1]][dims[2]];
        countBlocks();
        calculateCells();
        calculateModerators();
        optimizeCoolerSteps();
        calculateCoolers();
        this.heat = (int)heatf;
        this.power = (int)powerf;
        netHeat = this.heat-cooling;
        heatMult = (float)this.heat/cells/configuration.fuelHeat[fuel];
        efficiency = (float)this.power/cells/configuration.fuelPower[fuel];
    }
    private int countAdjacents(int x, int y, int z, boolean[] endTest, boolean[] pathTest, int distance){
        int count = 0;
        for(int[] direction : directions){
            count+=findAdjacent(x, y, z, direction[0], direction[1], direction[2], endTest, pathTest, distance);
        }
        return count;
    }
    private int findAdjacent(int x, int y, int z, int dx, int dy, int dz, boolean[] endTest, boolean[] pathTest, int distance){
        for(int dist = 0; dist<=distance; dist++){
            x+=dx;
            y+=dy;
            z+=dz;
            if(x<0||y<0||z<0||x>=dims[0]||y>=dims[1]||z>=dims[2]||blocks[x][y][z]==-1){
                return 0;//hit casing or air
            }
            if(endTest[blocks[x][y][z]]){
                for(int d = 0; d<=dist; d++){
                    blockValid[x-dx*d][y-dy*d][z-dz*d]++;
                }
                return 1;
            }
            else if(!pathTest[blocks[x][y][z]])return 0;
        }
        return 0;
    }
    @Override
    public void importAndConvert(UnderhaulSFR sfr){
        dims[0] = sfr.getInternalWidth();
        dims[1] = sfr.getInternalHeight();
        dims[2] = sfr.getInternalDepth();
        sfr.forEachInternalPosition((x, y, z) -> {
            Block block = sfr.getBlock(x, y, z);
            NCPFElementDefinition definition = block==null?null:block.template.definition;
            NCPFElementDefinition recipe = block==null||block.recipe==null?null:block.recipe.definition;
            int b = -1;
            for(int i = 0; i<configuration.blockDefinition.length; i++){
                if(configuration.blockDefinition[i].matches(definition)){
                    if(recipe==null||configuration.blockActive[i].matches(recipe))b = i;
                }
            }
            blocks[x-1][y-1][z-1] = b;
        });
        int f = 0;
        for(int i = 0; i<configuration.fuelDefinition.length; i++){
            if(configuration.fuelDefinition[i].matches(sfr.fuel.definition))f = i;
        }
        fuel = f;
    }
    @Override
    public String getTooltip(){
        return "Power Generation: "+power+"RF/t\n"
                + "Total Heat: "+heat+"H/t\n"
                + "Total Cooling: "+cooling+"H/t\n"
                + "Net Heat: "+netHeat+"H/t\n"
                + "Efficiency: "+MathUtil.percent(efficiency, 0)+"\n"
                + "Heat multiplier: "+MathUtil.percent(heatMult, 0)+"\n"
                + (cells>0?"Fuel burn time: "+configuration.fuelTime[fuel]/cells+"\n":"")
                + "Fuel cells: "+cells;
    }
    private void genVariables(){
        vars = new Variable[7+blockCount.length];
        vars[0] = new VariableInt("Net Heat"){
            @Override
            public int getValue(){
                return netHeat;
            }
        };
        vars[1] = new VariableInt("Total Output"){
            @Override
            public int getValue(){
                return power;
            }
        };
        vars[2] = new VariableInt("Total Heat"){
            @Override
            public int getValue(){
                return heat;
            }
        };
        vars[3] = new VariableInt("Total Cooling"){
            @Override
            public int getValue(){
                return cooling;
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
                return efficiency;
            }
        };
        vars[6] = new VariableFloat("Heat Multiplier"){
            @Override
            public float getValue(){
                return heatMult;
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
    }
    @Override
    public int getVariableCount(){
        if(vars==null)genVariables();
        return vars.length;
    }
    @Override
    public Variable getVariable(int i){
        if(vars==null)genVariables();
        return vars[i];
    }
    @Override
    public LiteUnderhaulSFR copy(){
        LiteUnderhaulSFR copy = new LiteUnderhaulSFR(configuration);
        copy.copyFrom(this);
        return copy;
    }
    @Override
    public void copyFrom(LiteMultiblock<UnderhaulSFR> other){
        LiteUnderhaulSFR sfr = (LiteUnderhaulSFR)other;
        fuel = sfr.fuel;
        dims[0] = sfr.dims[0];;
        dims[1] = sfr.dims[1];
        dims[2] = sfr.dims[2];
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                System.arraycopy(sfr.blocks[x][y], 0, blocks[x][y], 0, dims[2]);
            }
        }
    }
    @Override
    public void copyVarsFrom(LiteMultiblock<UnderhaulSFR> other){
        LiteUnderhaulSFR sfr = (LiteUnderhaulSFR)other;
        netHeat = sfr.netHeat;
        power = sfr.power;
        heat = sfr.heat;
        cooling = sfr.cooling;
        cells = sfr.cells;
        efficiency = sfr.efficiency;
        heatMult = sfr.heatMult;
        if(blockValid==null)blockValid = new int[dims[0]][dims[1]][dims[2]];
        if(blockEfficiency==null)blockEfficiency = new int[dims[0]][dims[1]][dims[2]];
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                System.arraycopy(sfr.blockValid[x][y], 0, blockValid[x][y], 0, dims[2]);
                System.arraycopy(sfr.blockEfficiency[x][y], 0, blockEfficiency[x][y], 0, dims[2]);
            }
        }
        System.arraycopy(sfr.blockCount, 0, blockCount, 0, blockCount.length);
    }
    @Override
    public UnderhaulSFR export(NCPFConfigurationContainer configg){
        UnderhaulSFRConfiguration config = configg.getConfiguration(UnderhaulSFRConfiguration::new);
        Fuel fuel = null;
        for(Fuel f : config.fuels){
            if(f.definition.matches(configuration.fuelDefinition[this.fuel])){
                fuel = f;
                break;
            }
        }
        calculate();
        UnderhaulSFR sfr = new UnderhaulSFR(configg, dims[0], dims[1], dims[2], fuel);
        sfr.forEachInternalPosition((x, y, z) -> {
            int block = blocks[x-1][y-1][z-1];
            if(blockValid[x-1][y-1][z-1]+blockEfficiency[x-1][y-1][z-1]<=0)block = -1;
            Block bl = null;
            if(block>=0){
                for(net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement b : config.blocks){
                    if(b.definition.matches(configuration.blockDefinition[block])){
                        bl = new Block(configg, x, y, z, b);
                        NCPFElementDefinition active = configuration.blockActive[block];
                        if(active!=null){
                            for(ActiveCoolerRecipe recipe : b.activeCoolerRecipes){
                                if(recipe.definition.matches(active))bl.recipe = recipe;
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
        if(blockValid==null||blockEfficiency==null)return null;
        if(blockValid[x][y][z]+blockEfficiency[x][y][z]<1)return null;
        int block = blocks[x][y][z];
        return block>=0?configuration.blockTexture[block]:null;
    }
    @Override
    public float getCubeBounds(int x, int y, int z, int index){
        if(index<3)return 0;
        return 1;
    }
    @Override
    public LiteGenerator<LiteUnderhaulSFR>[] createGenerators(LiteMultiblock<UnderhaulSFR> priorityMultiblock){
        ArrayList<LiteGenerator<LiteUnderhaulSFR>> gens = new ArrayList<>();
        {
        //<editor-fold defaultstate="collapsed" desc="Max Output">
        LiteGenerator<LiteUnderhaulSFR> gen = new LiteGenerator<>("Maximize Output");
        SettingInt reactorCount = new SettingInt("Reactor Count", 4);
        gen.parameters.add(reactorCount);
        SettingPercent minEfficiency = new SettingPercent("Min Efficiency", 6);
        gen.parameters.add(minEfficiency);
        SettingInt ctimeout = new SettingInt("Core Timeout (ms)", 500);
        gen.parameters.add(ctimeout);
        SettingInt timeout = new SettingInt("Timeout (ms)", 2_000);
        gen.parameters.add(timeout);
        SettingInt finalTimeout = new SettingInt("Final Timeout (ms)", 10_000);
        gen.parameters.add(finalTimeout);
        //<editor-fold defaultstate="collapsed" desc="Stage 1 - build core">
        {
            GeneratorStage<LiteUnderhaulSFR> stage = new GeneratorStage<>();
            {
                Priority<LiteUnderhaulSFR> valid = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(new ConstInt(1));
                min1.v2.set(priorityMultiblock.getVariable(1));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(new ConstInt(1));
                min2.v2.set(getVariable(1));
                validOp.v2.set(min2);
                valid.operator.set(validOp);
                stage.priorities.add(valid);
            }
            {
                Priority<LiteUnderhaulSFR> efficiencyFloor = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(minEfficiency);
                min1.v2.set(priorityMultiblock.getVariable(5));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(minEfficiency);
                min2.v2.set(getVariable(5));
                validOp.v2.set(min2);
                efficiencyFloor.operator.set(validOp);
                stage.priorities.add(efficiencyFloor);
            }
            {
                Priority<LiteUnderhaulSFR> output = new Priority<>();
                OperatorSubtraction outputOp = new OperatorSubtraction();
                outputOp.v1.set(priorityMultiblock.getVariable(1));
                outputOp.v2.set(getVariable(1));
                output.operator.set(outputOp);
                stage.priorities.add(output);
            }
            {
                Priority<LiteUnderhaulSFR> efficiency = new Priority<>();
                OperatorSubtraction efficiencyOp = new OperatorSubtraction();
                efficiencyOp.v1.set(priorityMultiblock.getVariable(5));
                efficiencyOp.v2.set(getVariable(5));
                efficiency.operator.set(efficiencyOp);
                stage.priorities.add(efficiency);
            }
            {
                RandomBlockMutator rbm = new RandomBlockMutator(this);
                Symmetry symmetry = rbm.symmetry.get();
                symmetry.mx.set(true);
                symmetry.my.set(true);
                symmetry.mz.set(true);
                ArrayList<Integer> indicies = new ArrayList<>();
                indicies.add(0);
                for(int i = 0; i<configuration.blockDefinition.length; i++){
                    if(configuration.blockCooling[i]==0)indicies.add(i+1);
                }
                int[] indcs = new int[indicies.size()];
                for(int i = 0; i<indicies.size(); i++){
                    indcs[i] = indicies.get(i);
                }
                rbm.indicies.set(indcs);
                RandomQuantityMutator<LiteUnderhaulSFR> mutator = new RandomQuantityMutator();
                mutator.mutator = rbm;
                mutator.max.set(Math.max(1,getDimension(0)*getDimension(1)*getDimension(2)/10));
                stage.steps.add(mutator);
            }
            {
                StageTransition<LiteUnderhaulSFR> transition = new StageTransition<>();
                transition.targetStage.set(1);
                ConditionGreaterEqual hits = new ConditionGreaterEqual();
                hits.v1.set(stage.getVariable(0));
                hits.v2.set(new ConstInt(1000));
                transition.conditions.add(hits);
                ConditionGreaterEqual time = new ConditionGreaterEqual();
                time.v1.set(gen.getVariable(2));
                time.v2.set(ctimeout);
                transition.conditions.add(time);
                stage.stageTransitions.add(transition);
            }
            gen.stages.add(stage);
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Stage 2 - Add heatsinks now">
        {
            GeneratorStage<LiteUnderhaulSFR> stage = new GeneratorStage<>();
            {
                Priority<LiteUnderhaulSFR> valid = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(new ConstInt(1));
                min1.v2.set(priorityMultiblock.getVariable(1));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(new ConstInt(1));
                min2.v2.set(getVariable(1));
                validOp.v2.set(min2);
                valid.operator.set(validOp);
                stage.priorities.add(valid);
            }
            {
                Priority<LiteUnderhaulSFR> efficiencyFloor = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(minEfficiency);
                min1.v2.set(priorityMultiblock.getVariable(5));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(minEfficiency);
                min2.v2.set(getVariable(5));
                validOp.v2.set(min2);
                efficiencyFloor.operator.set(validOp);
                stage.priorities.add(efficiencyFloor);
            }
            {
                Priority<LiteUnderhaulSFR> output = new Priority<>();
                OperatorSubtraction outputOp = new OperatorSubtraction();
                outputOp.v1.set(priorityMultiblock.getVariable(1));
                outputOp.v2.set(getVariable(1));
                output.operator.set(outputOp);
                stage.priorities.add(output);
            }
            {
                Priority<LiteUnderhaulSFR> stable = new Priority<>();
                OperatorSubtraction stableOp = new OperatorSubtraction();
                OperatorMaximum max1 = new OperatorMaximum();
                max1.v1.set(new ConstInt(0));
                max1.v2.set(getVariable(0));
                stableOp.v1.set(max1);
                OperatorMaximum max2 = new OperatorMaximum();
                max2.v1.set(new ConstInt(0));
                max2.v2.set(priorityMultiblock.getVariable(0));
                stableOp.v2.set(max2);
                stable.operator.set(stableOp);
                stage.priorities.add(stable);
            }
            {
                Priority<LiteUnderhaulSFR> efficiency = new Priority<>();
                OperatorSubtraction efficiencyOp = new OperatorSubtraction();
                efficiencyOp.v1.set(priorityMultiblock.getVariable(5));
                efficiencyOp.v2.set(getVariable(5));
                efficiency.operator.set(efficiencyOp);
                stage.priorities.add(efficiency);
            }
            {
                RandomBlockMutator rbm = new RandomBlockMutator(this);
                Symmetry symmetry = rbm.symmetry.get();
                symmetry.mx.set(true);
                symmetry.my.set(true);
                symmetry.mz.set(true);
                ArrayList<Integer> indicies = new ArrayList<>();
                indicies.add(0);
                for(int i = 0; i<configuration.blockDefinition.length; i++){
                    if(configuration.blockActive[i]==null)indicies.add(i+1);
                }
                int[] indcs = new int[indicies.size()];
                for(int i = 0; i<indicies.size(); i++){
                    indcs[i] = indicies.get(i);
                }
                rbm.indicies.set(indcs);
                RandomQuantityMutator<LiteUnderhaulSFR> mutator = new RandomQuantityMutator();
                mutator.mutator = rbm;
                mutator.max.set(Math.max(1,getDimension(0)*getDimension(1)*getDimension(2)/100));
                stage.steps.add(mutator);
            }
            {
                StageTransition<LiteUnderhaulSFR> transition = new StageTransition<>();
                transition.targetStage.set(2);
                ConditionGreaterEqual hits = new ConditionGreaterEqual();
                hits.v1.set(stage.getVariable(0));
                hits.v2.set(new ConstInt(1000));
                transition.conditions.add(hits);
                ConditionGreaterEqual time = new ConditionGreaterEqual();
                time.v1.set(gen.getVariable(2));
                time.v2.set(ctimeout);
                transition.conditions.add(time);
                stage.stageTransitions.add(transition);
            }
            gen.stages.add(stage);
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Stage 3 - Stabilize & Repeat">
        {
            GeneratorStage<LiteUnderhaulSFR> stage = new GeneratorStage<>();
            {
                Priority<LiteUnderhaulSFR> valid = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(new ConstInt(1));
                min1.v2.set(priorityMultiblock.getVariable(1));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(new ConstInt(1));
                min2.v2.set(getVariable(1));
                validOp.v2.set(min2);
                valid.operator.set(validOp);
                stage.priorities.add(valid);
            }
            {
                Priority<LiteUnderhaulSFR> stable = new Priority<>();
                OperatorSubtraction stableOp = new OperatorSubtraction();
                OperatorMaximum max1 = new OperatorMaximum();
                max1.v1.set(new ConstInt(0));
                max1.v2.set(getVariable(0));
                stableOp.v1.set(max1);
                OperatorMaximum max2 = new OperatorMaximum();
                max2.v1.set(new ConstInt(0));
                max2.v2.set(priorityMultiblock.getVariable(0));
                stableOp.v2.set(max2);
                stable.operator.set(stableOp);
                stage.priorities.add(stable);
            }
            {
                Priority<LiteUnderhaulSFR> efficiencyFloor = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(minEfficiency);
                min1.v2.set(priorityMultiblock.getVariable(5));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(minEfficiency);
                min2.v2.set(getVariable(5));
                validOp.v2.set(min2);
                efficiencyFloor.operator.set(validOp);
                stage.priorities.add(efficiencyFloor);
            }
            {
                Priority<LiteUnderhaulSFR> output = new Priority<>();
                OperatorSubtraction outputOp = new OperatorSubtraction();
                outputOp.v1.set(priorityMultiblock.getVariable(1));
                outputOp.v2.set(getVariable(1));
                output.operator.set(outputOp);
                stage.priorities.add(output);
            }
            {
                Priority<LiteUnderhaulSFR> efficiency = new Priority<>();
                OperatorSubtraction efficiencyOp = new OperatorSubtraction();
                efficiencyOp.v1.set(priorityMultiblock.getVariable(5));
                efficiencyOp.v2.set(getVariable(5));
                efficiency.operator.set(efficiencyOp);
                stage.priorities.add(efficiency);
            }
            SingleMutator<LiteUnderhaulSFR> singleMutator = new SingleMutator();
            singleMutator.mutator = new ClearInvalidMutator();
            stage.steps.add(singleMutator);
            {
                RandomBlockMutator rbm = new RandomBlockMutator(this);
                Symmetry symmetry = rbm.symmetry.get();
                symmetry.mx.set(true);
                symmetry.my.set(true);
                symmetry.mz.set(true);
                ArrayList<Integer> indicies = new ArrayList<>();
                indicies.add(0);
                for(int i = 0; i<configuration.blockDefinition.length; i++){
                    if(configuration.blockActive[i]==null)indicies.add(i+1);
                }
                int[] indcs = new int[indicies.size()];
                for(int i = 0; i<indicies.size(); i++){
                    indcs[i] = indicies.get(i);
                }
                rbm.indicies.set(indcs);
                RandomQuantityMutator<LiteUnderhaulSFR> mutator = new RandomQuantityMutator();
                mutator.mutator = rbm;
                mutator.max.set(Math.max(1,getDimension(0)*getDimension(1)*getDimension(2)/100));
                stage.steps.add(mutator);
            }
            {
                StageTransition<LiteUnderhaulSFR> transition = new StageTransition<>();
                transition.store.set(true);
                ConditionGreaterEqual hits = new ConditionGreaterEqual();
                hits.v1.set(stage.getVariable(0));
                hits.v2.set(new ConstInt(10000));
                transition.conditions.add(hits);
                ConditionGreaterEqual time = new ConditionGreaterEqual();
                time.v1.set(gen.getVariable(2));
                time.v2.set(timeout);
                transition.conditions.add(time);
                ConditionLessEqual heat = new ConditionLessEqual();
                heat.v1.set(getVariable(0));
                heat.v2.set(new ConstInt(0));
                transition.conditions.add(heat);
                ConditionLess stored = new ConditionLess();
                stored.v1.set(gen.getVariable(3));
                stored.v2.set(reactorCount);
                transition.conditions.add(stored);
                stage.stageTransitions.add(transition);
            }
            {
                StageTransition<LiteUnderhaulSFR> transition = new StageTransition<>();
                transition.targetStage.set(3);
                transition.consolidate.set(true);
                ConditionGreaterEqual hits = new ConditionGreaterEqual();
                hits.v1.set(stage.getVariable(0));
                hits.v2.set(new ConstInt(10000));
                transition.conditions.add(hits);
                ConditionGreaterEqual time = new ConditionGreaterEqual();
                time.v1.set(gen.getVariable(2));
                time.v2.set(timeout);
                transition.conditions.add(time);
                ConditionLessEqual heat = new ConditionLessEqual();
                heat.v1.set(getVariable(0));
                heat.v2.set(new ConstInt(0));
                transition.conditions.add(heat);
                ConditionGreaterEqual stored = new ConditionGreaterEqual();
                stored.v1.set(gen.getVariable(3));
                stored.v2.set(reactorCount);
                transition.conditions.add(stored);
                stage.stageTransitions.add(transition);
            }
            gen.stages.add(stage);
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Stage 4 - Finalize">
        {
            GeneratorStage<LiteUnderhaulSFR> stage = new GeneratorStage<>();
            {
                Priority<LiteUnderhaulSFR> valid = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(new ConstInt(1));
                min1.v2.set(priorityMultiblock.getVariable(1));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(new ConstInt(1));
                min2.v2.set(getVariable(1));
                validOp.v2.set(min2);
                valid.operator.set(validOp);
                stage.priorities.add(valid);
            }
            {
                Priority<LiteUnderhaulSFR> stable = new Priority<>();
                OperatorSubtraction stableOp = new OperatorSubtraction();
                OperatorMaximum max1 = new OperatorMaximum();
                max1.v1.set(new ConstInt(0));
                max1.v2.set(getVariable(0));
                stableOp.v1.set(max1);
                OperatorMaximum max2 = new OperatorMaximum();
                max2.v1.set(new ConstInt(0));
                max2.v2.set(priorityMultiblock.getVariable(0));
                stableOp.v2.set(max2);
                stable.operator.set(stableOp);
                stage.priorities.add(stable);
            }
            {
                Priority<LiteUnderhaulSFR> efficiencyFloor = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(minEfficiency);
                min1.v2.set(priorityMultiblock.getVariable(5));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(minEfficiency);
                min2.v2.set(getVariable(5));
                validOp.v2.set(min2);
                efficiencyFloor.operator.set(validOp);
                stage.priorities.add(efficiencyFloor);
            }
            {
                Priority<LiteUnderhaulSFR> output = new Priority<>();
                OperatorSubtraction outputOp = new OperatorSubtraction();
                outputOp.v1.set(priorityMultiblock.getVariable(1));
                outputOp.v2.set(getVariable(1));
                output.operator.set(outputOp);
                stage.priorities.add(output);
            }
            {
                Priority<LiteUnderhaulSFR> efficiency = new Priority<>();
                OperatorSubtraction efficiencyOp = new OperatorSubtraction();
                efficiencyOp.v1.set(priorityMultiblock.getVariable(5));
                efficiencyOp.v2.set(getVariable(5));
                efficiency.operator.set(efficiencyOp);
                stage.priorities.add(efficiency);
            }
            SingleMutator<LiteUnderhaulSFR> singleMutator = new SingleMutator();
            singleMutator.mutator = new ClearInvalidMutator();
            stage.steps.add(singleMutator);
            {
                RandomBlockMutator rbm = new RandomBlockMutator(this);
                Symmetry symmetry = rbm.symmetry.get();
                symmetry.mx.set(true);
                symmetry.my.set(true);
                symmetry.mz.set(true);
                ArrayList<Integer> indicies = new ArrayList<>();
                indicies.add(0);
                for(int i = 0; i<configuration.blockDefinition.length; i++){
                    if(configuration.blockActive[i]==null)indicies.add(i+1);
                }
                int[] indcs = new int[indicies.size()];
                for(int i = 0; i<indicies.size(); i++){
                    indcs[i] = indicies.get(i);
                }
                rbm.indicies.set(indcs);
                RandomQuantityMutator<LiteUnderhaulSFR> mutator = new RandomQuantityMutator();
                mutator.mutator = rbm;
                mutator.max.set(Math.max(1,getDimension(0)*getDimension(1)*getDimension(2)/100));
                stage.steps.add(mutator);
            }
            {
                StageTransition<LiteUnderhaulSFR> transition = new StageTransition<>();
                transition.stop.set(true);
                ConditionGreaterEqual hits = new ConditionGreaterEqual();
                hits.v1.set(stage.getVariable(0));
                hits.v2.set(new ConstInt(10000));
                transition.conditions.add(hits);
                ConditionGreaterEqual time = new ConditionGreaterEqual();
                time.v1.set(gen.getVariable(2));
                time.v2.set(finalTimeout);
                transition.conditions.add(time);
                ConditionLessEqual heat = new ConditionLessEqual();
                heat.v1.set(getVariable(0));
                heat.v2.set(new ConstInt(0));
                transition.conditions.add(heat);
                stage.stageTransitions.add(transition);
            }
            gen.stages.add(stage);
        }
        //</editor-fold>
        gens.add(gen);
        //</editor-fold>
        }
        {
        //<editor-fold defaultstate="collapsed" desc="Max Efficiency">
        LiteGenerator<LiteUnderhaulSFR> gen = new LiteGenerator<>("Maximize Efficiency");
        SettingInt reactorCount = new SettingInt("Reactor Count", 4);
        gen.parameters.add(reactorCount);
        SettingInt minOutput = new SettingInt("Min Output", 1);
        gen.parameters.add(minOutput);
        SettingInt ctimeout = new SettingInt("Core Timeout (ms)", 500);
        gen.parameters.add(ctimeout);
        SettingInt timeout = new SettingInt("Timeout (ms)", 2_000);
        gen.parameters.add(timeout);
        SettingInt finalTimeout = new SettingInt("Final Timeout (ms)", 10_000);
        gen.parameters.add(finalTimeout);
        //<editor-fold defaultstate="collapsed" desc="Stage 1 - build core">
        {
            GeneratorStage<LiteUnderhaulSFR> stage = new GeneratorStage<>();
            {
                Priority<LiteUnderhaulSFR> valid = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(new ConstInt(1));
                min1.v2.set(priorityMultiblock.getVariable(1));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(new ConstInt(1));
                min2.v2.set(getVariable(1));
                validOp.v2.set(min2);
                valid.operator.set(validOp);
                stage.priorities.add(valid);
            }
            {
                Priority<LiteUnderhaulSFR> outputFloor = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(minOutput);
                min1.v2.set(priorityMultiblock.getVariable(1));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(minOutput);
                min2.v2.set(getVariable(1));
                validOp.v2.set(min2);
                outputFloor.operator.set(validOp);
                stage.priorities.add(outputFloor);
            }
            {
                Priority<LiteUnderhaulSFR> efficiency = new Priority<>();
                OperatorSubtraction efficiencyOp = new OperatorSubtraction();
                efficiencyOp.v1.set(priorityMultiblock.getVariable(5));
                efficiencyOp.v2.set(getVariable(5));
                efficiency.operator.set(efficiencyOp);
                stage.priorities.add(efficiency);
            }
            {
                Priority<LiteUnderhaulSFR> output = new Priority<>();
                OperatorSubtraction outputOp = new OperatorSubtraction();
                outputOp.v1.set(priorityMultiblock.getVariable(1));
                outputOp.v2.set(getVariable(1));
                output.operator.set(outputOp);
                stage.priorities.add(output);
            }
            {
                RandomBlockMutator rbm = new RandomBlockMutator(this);
                Symmetry symmetry = rbm.symmetry.get();
                symmetry.mx.set(true);
                symmetry.my.set(true);
                symmetry.mz.set(true);
                ArrayList<Integer> indicies = new ArrayList<>();
                indicies.add(0);
                for(int i = 0; i<configuration.blockDefinition.length; i++){
                    if(configuration.blockCooling[i]==0)indicies.add(i+1);
                }
                int[] indcs = new int[indicies.size()];
                for(int i = 0; i<indicies.size(); i++){
                    indcs[i] = indicies.get(i);
                }
                rbm.indicies.set(indcs);
                RandomQuantityMutator<LiteUnderhaulSFR> mutator = new RandomQuantityMutator();
                mutator.mutator = rbm;
                mutator.max.set(Math.max(1,getDimension(0)*getDimension(1)*getDimension(2)/10));
                stage.steps.add(mutator);
            }
            {
                StageTransition<LiteUnderhaulSFR> transition = new StageTransition<>();
                transition.targetStage.set(1);
                ConditionGreaterEqual hits = new ConditionGreaterEqual();
                hits.v1.set(stage.getVariable(0));
                hits.v2.set(new ConstInt(1000));
                transition.conditions.add(hits);
                ConditionGreaterEqual time = new ConditionGreaterEqual();
                time.v1.set(gen.getVariable(2));
                time.v2.set(ctimeout);
                transition.conditions.add(time);
                stage.stageTransitions.add(transition);
            }
            gen.stages.add(stage);
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Stage 2 - Add heatsinks now">
        {
            GeneratorStage<LiteUnderhaulSFR> stage = new GeneratorStage<>();
            {
                Priority<LiteUnderhaulSFR> valid = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(new ConstInt(1));
                min1.v2.set(priorityMultiblock.getVariable(1));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(new ConstInt(1));
                min2.v2.set(getVariable(1));
                validOp.v2.set(min2);
                valid.operator.set(validOp);
                stage.priorities.add(valid);
            }
            {
                Priority<LiteUnderhaulSFR> outputFloor = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(minOutput);
                min1.v2.set(priorityMultiblock.getVariable(1));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(minOutput);
                min2.v2.set(getVariable(1));
                validOp.v2.set(min2);
                outputFloor.operator.set(validOp);
                stage.priorities.add(outputFloor);
            }
            {
                Priority<LiteUnderhaulSFR> efficiency = new Priority<>();
                OperatorSubtraction efficiencyOp = new OperatorSubtraction();
                efficiencyOp.v1.set(priorityMultiblock.getVariable(5));
                efficiencyOp.v2.set(getVariable(5));
                efficiency.operator.set(efficiencyOp);
                stage.priorities.add(efficiency);
            }
            {
                Priority<LiteUnderhaulSFR> stable = new Priority<>();
                OperatorSubtraction stableOp = new OperatorSubtraction();
                OperatorMaximum max1 = new OperatorMaximum();
                max1.v1.set(new ConstInt(0));
                max1.v2.set(getVariable(0));
                stableOp.v1.set(max1);
                OperatorMaximum max2 = new OperatorMaximum();
                max2.v1.set(new ConstInt(0));
                max2.v2.set(priorityMultiblock.getVariable(0));
                stableOp.v2.set(max2);
                stable.operator.set(stableOp);
                stage.priorities.add(stable);
            }
            {
                Priority<LiteUnderhaulSFR> output = new Priority<>();
                OperatorSubtraction outputOp = new OperatorSubtraction();
                outputOp.v1.set(priorityMultiblock.getVariable(1));
                outputOp.v2.set(getVariable(1));
                output.operator.set(outputOp);
                stage.priorities.add(output);
            }
            {
                RandomBlockMutator rbm = new RandomBlockMutator(this);
                Symmetry symmetry = rbm.symmetry.get();
                symmetry.mx.set(true);
                symmetry.my.set(true);
                symmetry.mz.set(true);
                ArrayList<Integer> indicies = new ArrayList<>();
                indicies.add(0);
                for(int i = 0; i<configuration.blockDefinition.length; i++){
                    if(configuration.blockActive[i]==null)indicies.add(i+1);
                }
                int[] indcs = new int[indicies.size()];
                for(int i = 0; i<indicies.size(); i++){
                    indcs[i] = indicies.get(i);
                }
                rbm.indicies.set(indcs);
                RandomQuantityMutator<LiteUnderhaulSFR> mutator = new RandomQuantityMutator();
                mutator.mutator = rbm;
                mutator.max.set(Math.max(1,getDimension(0)*getDimension(1)*getDimension(2)/100));
                stage.steps.add(mutator);
            }
            {
                StageTransition<LiteUnderhaulSFR> transition = new StageTransition<>();
                transition.targetStage.set(2);
                ConditionGreaterEqual hits = new ConditionGreaterEqual();
                hits.v1.set(stage.getVariable(0));
                hits.v2.set(new ConstInt(1000));
                transition.conditions.add(hits);
                ConditionGreaterEqual time = new ConditionGreaterEqual();
                time.v1.set(gen.getVariable(2));
                time.v2.set(ctimeout);
                transition.conditions.add(time);
                stage.stageTransitions.add(transition);
            }
            gen.stages.add(stage);
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Stage 3 - Stabilize & Repeat">
        {
            GeneratorStage<LiteUnderhaulSFR> stage = new GeneratorStage<>();
            {
                Priority<LiteUnderhaulSFR> valid = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(new ConstInt(1));
                min1.v2.set(priorityMultiblock.getVariable(1));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(new ConstInt(1));
                min2.v2.set(getVariable(1));
                validOp.v2.set(min2);
                valid.operator.set(validOp);
                stage.priorities.add(valid);
            }
            {
                Priority<LiteUnderhaulSFR> stable = new Priority<>();
                OperatorSubtraction stableOp = new OperatorSubtraction();
                OperatorMaximum max1 = new OperatorMaximum();
                max1.v1.set(new ConstInt(0));
                max1.v2.set(getVariable(0));
                stableOp.v1.set(max1);
                OperatorMaximum max2 = new OperatorMaximum();
                max2.v1.set(new ConstInt(0));
                max2.v2.set(priorityMultiblock.getVariable(0));
                stableOp.v2.set(max2);
                stable.operator.set(stableOp);
                stage.priorities.add(stable);
            }
            {
                Priority<LiteUnderhaulSFR> outputFloor = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(minOutput);
                min1.v2.set(priorityMultiblock.getVariable(1));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(minOutput);
                min2.v2.set(getVariable(1));
                validOp.v2.set(min2);
                outputFloor.operator.set(validOp);
                stage.priorities.add(outputFloor);
            }
            {
                Priority<LiteUnderhaulSFR> efficiency = new Priority<>();
                OperatorSubtraction efficiencyOp = new OperatorSubtraction();
                efficiencyOp.v1.set(priorityMultiblock.getVariable(5));
                efficiencyOp.v2.set(getVariable(5));
                efficiency.operator.set(efficiencyOp);
                stage.priorities.add(efficiency);
            }
            {
                Priority<LiteUnderhaulSFR> output = new Priority<>();
                OperatorSubtraction outputOp = new OperatorSubtraction();
                outputOp.v1.set(priorityMultiblock.getVariable(1));
                outputOp.v2.set(getVariable(1));
                output.operator.set(outputOp);
                stage.priorities.add(output);
            }
            SingleMutator<LiteUnderhaulSFR> singleMutator = new SingleMutator();
            singleMutator.mutator = new ClearInvalidMutator();
            stage.steps.add(singleMutator);
            {
                RandomBlockMutator rbm = new RandomBlockMutator(this);
                Symmetry symmetry = rbm.symmetry.get();
                symmetry.mx.set(true);
                symmetry.my.set(true);
                symmetry.mz.set(true);
                ArrayList<Integer> indicies = new ArrayList<>();
                indicies.add(0);
                for(int i = 0; i<configuration.blockDefinition.length; i++){
                    if(configuration.blockActive[i]==null)indicies.add(i+1);
                }
                int[] indcs = new int[indicies.size()];
                for(int i = 0; i<indicies.size(); i++){
                    indcs[i] = indicies.get(i);
                }
                rbm.indicies.set(indcs);
                RandomQuantityMutator<LiteUnderhaulSFR> mutator = new RandomQuantityMutator();
                mutator.mutator = rbm;
                mutator.max.set(Math.max(1,getDimension(0)*getDimension(1)*getDimension(2)/100));
                stage.steps.add(mutator);
            }
            {
                Priority<LiteUnderhaulSFR> stable = new Priority<>();
                OperatorSubtraction stableOp = new OperatorSubtraction();
                OperatorMaximum max1 = new OperatorMaximum();
                max1.v1.set(new ConstInt(0));
                max1.v2.set(getVariable(0));
                stableOp.v1.set(max1);
                OperatorMaximum max2 = new OperatorMaximum();
                max2.v1.set(new ConstInt(0));
                max2.v2.set(priorityMultiblock.getVariable(0));
                stableOp.v2.set(max2);
                stable.operator.set(stableOp);
                stage.priorities.add(stable);
            }
            {
                StageTransition<LiteUnderhaulSFR> transition = new StageTransition<>();
                transition.store.set(true);
                ConditionGreaterEqual hits = new ConditionGreaterEqual();
                hits.v1.set(stage.getVariable(0));
                hits.v2.set(new ConstInt(10000));
                transition.conditions.add(hits);
                ConditionGreaterEqual time = new ConditionGreaterEqual();
                time.v1.set(gen.getVariable(2));
                time.v2.set(timeout);
                transition.conditions.add(time);
                ConditionLessEqual heat = new ConditionLessEqual();
                heat.v1.set(getVariable(0));
                heat.v2.set(new ConstInt(0));
                transition.conditions.add(heat);
                ConditionLess stored = new ConditionLess();
                stored.v1.set(gen.getVariable(3));
                stored.v2.set(reactorCount);
                transition.conditions.add(stored);
                stage.stageTransitions.add(transition);
            }
            {
                StageTransition<LiteUnderhaulSFR> transition = new StageTransition<>();
                transition.targetStage.set(3);
                transition.consolidate.set(true);
                ConditionGreaterEqual hits = new ConditionGreaterEqual();
                hits.v1.set(stage.getVariable(0));
                hits.v2.set(new ConstInt(10000));
                transition.conditions.add(hits);
                ConditionGreaterEqual time = new ConditionGreaterEqual();
                time.v1.set(gen.getVariable(2));
                time.v2.set(timeout);
                transition.conditions.add(time);
                ConditionLessEqual heat = new ConditionLessEqual();
                heat.v1.set(getVariable(0));
                heat.v2.set(new ConstInt(0));
                transition.conditions.add(heat);
                ConditionGreaterEqual stored = new ConditionGreaterEqual();
                stored.v1.set(gen.getVariable(3));
                stored.v2.set(reactorCount);
                transition.conditions.add(stored);
                stage.stageTransitions.add(transition);
            }
            gen.stages.add(stage);
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Stage 4 - Finalize">
        {
            GeneratorStage<LiteUnderhaulSFR> stage = new GeneratorStage<>();
            {
                Priority<LiteUnderhaulSFR> valid = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(new ConstInt(1));
                min1.v2.set(priorityMultiblock.getVariable(1));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(new ConstInt(1));
                min2.v2.set(getVariable(1));
                validOp.v2.set(min2);
                valid.operator.set(validOp);
                stage.priorities.add(valid);
            }
            {
                Priority<LiteUnderhaulSFR> stable = new Priority<>();
                OperatorSubtraction stableOp = new OperatorSubtraction();
                OperatorMaximum max1 = new OperatorMaximum();
                max1.v1.set(new ConstInt(0));
                max1.v2.set(getVariable(0));
                stableOp.v1.set(max1);
                OperatorMaximum max2 = new OperatorMaximum();
                max2.v1.set(new ConstInt(0));
                max2.v2.set(priorityMultiblock.getVariable(0));
                stableOp.v2.set(max2);
                stable.operator.set(stableOp);
                stage.priorities.add(stable);
            }
            {
                Priority<LiteUnderhaulSFR> outputFloor = new Priority<>();
                OperatorSubtraction validOp = new OperatorSubtraction();
                OperatorMinimum min1 = new OperatorMinimum();
                min1.v1.set(minOutput);
                min1.v2.set(priorityMultiblock.getVariable(1));
                validOp.v1.set(min1);
                OperatorMinimum min2 = new OperatorMinimum();
                min2.v1.set(minOutput);
                min2.v2.set(getVariable(1));
                validOp.v2.set(min2);
                outputFloor.operator.set(validOp);
                stage.priorities.add(outputFloor);
            }
            {
                Priority<LiteUnderhaulSFR> efficiency = new Priority<>();
                OperatorSubtraction efficiencyOp = new OperatorSubtraction();
                efficiencyOp.v1.set(priorityMultiblock.getVariable(5));
                efficiencyOp.v2.set(getVariable(5));
                efficiency.operator.set(efficiencyOp);
                stage.priorities.add(efficiency);
            }
            {
                Priority<LiteUnderhaulSFR> output = new Priority<>();
                OperatorSubtraction outputOp = new OperatorSubtraction();
                outputOp.v1.set(priorityMultiblock.getVariable(1));
                outputOp.v2.set(getVariable(1));
                output.operator.set(outputOp);
                stage.priorities.add(output);
            }
            SingleMutator<LiteUnderhaulSFR> singleMutator = new SingleMutator();
            singleMutator.mutator = new ClearInvalidMutator();
            stage.steps.add(singleMutator);
            {
                RandomBlockMutator rbm = new RandomBlockMutator(this);
                Symmetry symmetry = rbm.symmetry.get();
                symmetry.mx.set(true);
                symmetry.my.set(true);
                symmetry.mz.set(true);
                ArrayList<Integer> indicies = new ArrayList<>();
                indicies.add(0);
                for(int i = 0; i<configuration.blockDefinition.length; i++){
                    if(configuration.blockActive[i]==null)indicies.add(i+1);
                }
                int[] indcs = new int[indicies.size()];
                for(int i = 0; i<indicies.size(); i++){
                    indcs[i] = indicies.get(i);
                }
                rbm.indicies.set(indcs);
                RandomQuantityMutator<LiteUnderhaulSFR> mutator = new RandomQuantityMutator();
                mutator.mutator = rbm;
                mutator.max.set(Math.max(1,getDimension(0)*getDimension(1)*getDimension(2)/100));
                stage.steps.add(mutator);
            }
            {
                Priority<LiteUnderhaulSFR> stable = new Priority<>();
                OperatorSubtraction stableOp = new OperatorSubtraction();
                OperatorMaximum max1 = new OperatorMaximum();
                max1.v1.set(new ConstInt(0));
                max1.v2.set(getVariable(0));
                stableOp.v1.set(max1);
                OperatorMaximum max2 = new OperatorMaximum();
                max2.v1.set(new ConstInt(0));
                max2.v2.set(priorityMultiblock.getVariable(0));
                stableOp.v2.set(max2);
                stable.operator.set(stableOp);
                stage.priorities.add(stable);
            }
            {
                StageTransition<LiteUnderhaulSFR> transition = new StageTransition<>();
                transition.stop.set(true);
                ConditionGreaterEqual hits = new ConditionGreaterEqual();
                hits.v1.set(stage.getVariable(0));
                hits.v2.set(new ConstInt(10000));
                transition.conditions.add(hits);
                ConditionGreaterEqual time = new ConditionGreaterEqual();
                time.v1.set(gen.getVariable(2));
                time.v2.set(timeout);
                transition.conditions.add(time);
                ConditionLessEqual heat = new ConditionLessEqual();
                heat.v1.set(getVariable(0));
                heat.v2.set(new ConstInt(0));
                transition.conditions.add(heat);
                stage.stageTransitions.add(transition);
            }
            gen.stages.add(stage);
        }
        //</editor-fold>
        gens.add(gen);
        //</editor-fold>
        }
        return gens.toArray(new LiteGenerator[gens.size()]);
    }
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
}