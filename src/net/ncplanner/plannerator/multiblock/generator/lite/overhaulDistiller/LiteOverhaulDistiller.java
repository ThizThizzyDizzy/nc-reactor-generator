package net.ncplanner.plannerator.multiblock.generator.lite.overhaulDistiller;
import com.thizthizzydizzy.dizzyengine.graphics.image.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableFloat;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableInt;
import net.ncplanner.plannerator.multiblock.overhaul.distiller.Block;
import net.ncplanner.plannerator.multiblock.overhaul.distiller.OverhaulDistiller;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulDistillerConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.DistillerRecipe;
public class LiteOverhaulDistiller extends LiteMultiblock<OverhaulDistiller>{
    public final CompiledOverhaulDistillerConfiguration configuration;
    public final int[] dims;
    // External coordinates include the casing, whose units affect performance.
    public final int[][][] blocks;
    public boolean[][][] blockValid;
    public int recipe = -1;
    public int numControllers, missingCasings, inputPortCount, outputPortCount;
    public int traySieveCount, packedSieveCount, refluxUnitCount, reboilingUnitCount, liquidDistributorCount;
    public double traySieveEfficiency, packedSieveEfficiency, baseSpeedMultiplier, basePowerMultiplier;
    public double refluxUnitBonus, reboilingUnitBonus, liquidDistributorBonus, speedMultiplier;
    public double processTime, processPower, processEnergy;
    public boolean structureValid;
    public final List<String> errors = new ArrayList<>();
    private final int[] blockCount;
    public LiteOverhaulDistiller(CompiledOverhaulDistillerConfiguration configuration){
        this.configuration = configuration;
        dims = new int[]{configuration.minSize, configuration.minSize, configuration.minSize};
        int size = configuration.maxSize+2;
        blocks = new int[size][size][size];
        blockCount = new int[configuration.blockDefinition.length];
        for(int[][] plane : blocks)for(int[] row : plane)Arrays.fill(row, -1);
    }
    private static int find(NCPFElementDefinition[] definitions, NCPFElementDefinition definition){
        if(definition!=null)for(int i = 0; i<definitions.length; i++)if(definitions[i].matches(definition))return i;
        return -1;
    }
    @Override
    public void importAndConvert(OverhaulDistiller distiller){
        dims[0] = distiller.getInternalWidth();
        dims[1] = distiller.getInternalHeight();
        dims[2] = distiller.getInternalDepth();
        recipe = -1;
        if(distiller.recipe!=null)for(int i = 0; i<configuration.recipeDefinition.length; i++){
            if(CompiledOverhaulDistillerConfiguration.matchesRecipe(configuration.recipeDefinition[i], distiller.recipe.definition)){recipe = i;break;}
        }
        // Like the reactor lite models, generate the default casing for an unfinished design.
        OverhaulDistiller source = distiller.doCopy();
        source.buildDefaultCasing();
        source.forEachPosition(pos -> {
            Block b = source.getBlock(pos);
            blocks[pos.x][pos.y][pos.z] = find(configuration.blockDefinition, b==null?null:b.template.definition);
        });
    }
    private void error(String message){
        structureValid = false;
        if(!errors.contains(message))errors.add(message);
    }
    @Override
    public void calculate(){
        numControllers = missingCasings = inputPortCount = outputPortCount = 0;
        traySieveCount = packedSieveCount = refluxUnitCount = reboilingUnitCount = liquidDistributorCount = 0;
        traySieveEfficiency = packedSieveEfficiency = baseSpeedMultiplier = basePowerMultiplier = 0;
        refluxUnitBonus = reboilingUnitBonus = liquidDistributorBonus = speedMultiplier = 0;
        processTime = processPower = processEnergy = 0;
        structureValid = true;
        errors.clear();
        Arrays.fill(blockCount, 0);
        blockValid = new boolean[dims[0]+2][dims[1]+2][dims[2]+2];
        CompiledOverhaulDistillerConfiguration c = configuration;
        for(int x = 0; x<dims[0]+2; x++)for(int y = 0; y<dims[1]+2; y++)for(int z = 0; z<dims[2]+2; z++){
            int b = blocks[x][y][z];
            if(b>=0)blockCount[b]++;
            int boundaries = (x==0||x==dims[0]+1?1:0)+(y==0||y==dims[1]+1?1:0)+(z==0||z==dims[2]+1?1:0);
            if(boundaries==0)continue;
            if(b<0){
                missingCasings++;
                continue;
            }
            boolean valid = c.blockCasing[b]&&(boundaries==1||c.blockEdge[b]);
            if(c.blockController[b])numControllers++;
            if(c.blockReflux[b]||c.blockDistributor[b]){
                valid &= y==dims[1]+1;
                if(!valid)error("Reflux units and liquid distributors must be on the top face.");
            }
            if(c.blockReboiling[b]){
                valid &= y==0;
                if(!valid)error("Reboiling units must be on the bottom face.");
            }
            blockValid[x][y][z] = valid;
            if(!valid)error("Invalid casing block placement.");
            else{
                if(c.blockInputPort[b])inputPortCount++;
                if(c.blockOutputPort[b])outputPortCount++;
                if(c.blockReflux[b])refluxUnitCount++;
                if(c.blockReboiling[b])reboilingUnitCount++;
                if(c.blockDistributor[b])liquidDistributorCount++;
            }
        }
        if(numControllers!=1)error(numControllers==0?"No controller!":"Too many controllers!");
        if(missingCasings>0)error("Casing incomplete! (Missing "+missingCasings+")");
        if(recipe>=0){
            checkPorts(false, c.recipeInputPorts[recipe]-inputPortCount);
            checkPorts(true, c.recipeOutputPorts[recipe]-outputPortCount);
        }
        int[] layers = new int[dims[1]+2];
        boolean[] uniform = new boolean[layers.length];
        for(int y = 1; y<=dims[1]; y++){
            layers[y] = blocks[1][y][1];
            uniform[y] = true;
            for(int x = 1; x<=dims[0]; x++)for(int z = 1; z<=dims[2]; z++)if(blocks[x][y][z]!=layers[y])uniform[y] = false;
        }
        for(int y = 1; y<=dims[1]; y++){
            int b = layers[y];
            boolean valid = uniform[y]&&(b<0||c.blockTray[b]||c.blockSieve[b]);
            if(!uniform[y])error("Layer "+y+" must be uniform across its entire area.");
            if(b>=0&&c.blockTray[b]){
                boolean hasSieve = y<dims[1]&&uniform[y+1]&&layers[y+1]>=0&&c.blockSieve[layers[y+1]];
                valid &= hasSieve;
                if(!hasSieve)error("Sieve tray on layer "+y+" needs a complete sieve assembly layer directly above it.");
                if(valid){
                    traySieveCount++;
                    traySieveEfficiency += c.blockEfficiency[layers[y+1]];
                }
            }else if(valid&&b>=0&&c.blockSieve[b]){
                if(y==1||layers[y-1]<0||!c.blockTray[layers[y-1]]){
                    packedSieveCount++;
                    packedSieveEfficiency += c.blockEfficiency[b];
                }
            }
            for(int x = 1; x<=dims[0]; x++)for(int z = 1; z<=dims[2]; z++)blockValid[x][y][z] = valid;
            if(!valid)error("Invalid interior layer "+y+".");
        }
        int area = dims[0]*dims[2];
        baseSpeedMultiplier = area*(traySieveCount==0?packedSieveEfficiency:packedSieveCount==0?traySieveEfficiency:
                (packedSieveCount*traySieveEfficiency+packedSieveEfficiency)/(1+packedSieveCount));
        basePowerMultiplier = reboilingUnitCount+(double)area*(traySieveCount+packedSieveCount);
        if(recipe<0||!c.recipeHasStats[recipe])errors.add("No recipe selected!");
        else{
            refluxUnitBonus = (double)refluxUnitCount/area*c.recipeGasFraction[recipe];
            reboilingUnitBonus = (double)reboilingUnitCount/area*c.recipeLiquidFraction[recipe];
            liquidDistributorBonus = (double)liquidDistributorCount/area/(1+traySieveCount);
            speedMultiplier = baseSpeedMultiplier*(1+refluxUnitBonus)*(1+reboilingUnitBonus)*(1+liquidDistributorBonus);
            if(traySieveCount<c.recipeTrays[recipe])errors.add("Insufficient sieve trays! ("+traySieveCount+"/"+c.recipeTrays[recipe]+")");
            if(speedMultiplier<=0)errors.add("No effective sieve layers!");
            processPower = c.basePower*c.recipePower[recipe]*basePowerMultiplier;
            if(speedMultiplier>0){
                processTime = c.baseTime*c.recipeTime[recipe]/speedMultiplier;
                processEnergy = processTime*processPower;
            }
        }
    }
    private void checkPorts(boolean output, int missing){
        if(missing>0)error("Missing "+missing+(output?" output":" input")+" port"+(missing==1?"":"s")+"!");
    }
    @Override
    public String getTooltip(){
        return (errors.isEmpty()?"":String.join("\n", errors)+"\n")+"Sieve trays: "+traySieveCount+"\nPacked sieve layers: "+packedSieveCount
                +"\nSpeed multiplier: "+MathUtil.round(speedMultiplier, 3)+"\nPower multiplier: "+MathUtil.round(basePowerMultiplier, 3)
                +"\nReflux bonus: "+MathUtil.percent(refluxUnitBonus, 2)+"\nReboiling bonus: "+MathUtil.percent(reboilingUnitBonus, 2)
                +"\nLiquid distributor bonus: "+MathUtil.percent(liquidDistributorBonus, 2)+"\nPower use: "+MathUtil.round(processPower, 3)+" RF/t"
                +(recipe>=0&&configuration.recipeHasStats[recipe]&&speedMultiplier>0?"\nProcess time: "+MathUtil.round(processTime, 3)
                +" ticks\nEnergy per process: "+MathUtil.round(processEnergy, 3)+" RF":"");
    }
    private VariableInt integer(String name, IntSupplier value){
        return new VariableInt(name){public int getValue(){return value.getAsInt();}};
    }
    private VariableFloat decimal(String name, DoubleSupplier value){
        return new VariableFloat(name){public float getValue(){return (float)value.getAsDouble();}};
    }
    @Override
    public Variable[] genVariables(){
        ArrayList<Variable> vars = new ArrayList<>();
        vars.add(integer("Valid", () -> errors.isEmpty()?1:0));
        vars.add(integer("Error Count", () -> errors.size()));
        vars.add(integer("Sieve Trays", () -> traySieveCount));
        vars.add(integer("Missing Sieve Trays", () -> recipe<0?0:Math.max(0, configuration.recipeTrays[recipe]-traySieveCount)));
        vars.add(integer("Packed Sieve Layers", () -> packedSieveCount));
        vars.add(integer("Reflux Units", () -> refluxUnitCount));
        vars.add(integer("Reboiling Units", () -> reboilingUnitCount));
        vars.add(integer("Liquid Distributors", () -> liquidDistributorCount));
        vars.add(decimal("Speed Multiplier", () -> speedMultiplier));
        vars.add(decimal("Power Multiplier", () -> basePowerMultiplier));
        vars.add(decimal("Reflux Bonus", () -> refluxUnitBonus));
        vars.add(decimal("Reboiling Bonus", () -> reboilingUnitBonus));
        vars.add(decimal("Liquid Distributor Bonus", () -> liquidDistributorBonus));
        vars.add(decimal("Process Time", () -> processTime));
        vars.add(decimal("Power Use", () -> processPower));
        vars.add(decimal("Energy Per Process", () -> processEnergy));
        for(int i = 0; i<blockCount.length; i++){
            final int index = i;
            vars.add(integer("Block Count: "+configuration.blockDefinition[i], () -> blockCount[index]));
        }
        return vars.toArray(new Variable[vars.size()]);
    }
    @Override
    public LiteOverhaulDistiller copy(){
        LiteOverhaulDistiller copy = new LiteOverhaulDistiller(configuration);
        copy.copyFrom(this);
        return copy;
    }
    @Override
    public void copyFrom(LiteMultiblock<OverhaulDistiller> other){
        LiteOverhaulDistiller d = (LiteOverhaulDistiller)other;
        recipe = d.recipe;
        System.arraycopy(d.dims, 0, dims, 0, 3);
        for(int x = 0; x<dims[0]+2; x++)for(int y = 0; y<dims[1]+2; y++)System.arraycopy(d.blocks[x][y], 0, blocks[x][y], 0, dims[2]+2);
    }
    @Override
    public void copyVarsFrom(LiteMultiblock<OverhaulDistiller> other){
        LiteOverhaulDistiller d = (LiteOverhaulDistiller)other;
        numControllers = d.numControllers;
        missingCasings = d.missingCasings;
        inputPortCount = d.inputPortCount;
        outputPortCount = d.outputPortCount;
        traySieveCount = d.traySieveCount;
        packedSieveCount = d.packedSieveCount;
        refluxUnitCount = d.refluxUnitCount;
        reboilingUnitCount = d.reboilingUnitCount;
        liquidDistributorCount = d.liquidDistributorCount;
        traySieveEfficiency = d.traySieveEfficiency;
        packedSieveEfficiency = d.packedSieveEfficiency;
        baseSpeedMultiplier = d.baseSpeedMultiplier;
        basePowerMultiplier = d.basePowerMultiplier;
        refluxUnitBonus = d.refluxUnitBonus;
        reboilingUnitBonus = d.reboilingUnitBonus;
        liquidDistributorBonus = d.liquidDistributorBonus;
        speedMultiplier = d.speedMultiplier;
        processTime = d.processTime;
        processPower = d.processPower;
        processEnergy = d.processEnergy;
        structureValid = d.structureValid;
        errors.clear();
        errors.addAll(d.errors);
        System.arraycopy(d.blockCount, 0, blockCount, 0, blockCount.length);
        blockValid = new boolean[dims[0]+2][dims[1]+2][dims[2]+2];
        if(d.blockValid!=null)for(int x = 0; x<dims[0]+2; x++)for(int y = 0; y<dims[1]+2; y++)System.arraycopy(d.blockValid[x][y], 0, blockValid[x][y], 0, dims[2]+2);
    }
    @Override
    public OverhaulDistiller export(NCPFConfigurationContainer container){
        OverhaulDistillerConfiguration config = container.getConfiguration(OverhaulDistillerConfiguration::new);
        DistillerRecipe r = null;
        if(recipe>=0)for(DistillerRecipe candidate : config.recipes)if(CompiledOverhaulDistillerConfiguration.matchesRecipe(candidate.definition, configuration.recipeDefinition[recipe])){r = candidate;break;}
        if(recipe>=0&&r==null)throw new IllegalArgumentException("Distiller recipe missing from export configuration");
        BlockElement[] templates = new BlockElement[configuration.blockDefinition.length];
        for(int i = 0; i<templates.length; i++)for(BlockElement b : config.blocks)if(b.definition.matches(configuration.blockDefinition[i])){templates[i] = b;break;}
        OverhaulDistiller d = new OverhaulDistiller(container, dims[0], dims[1], dims[2], r);
        d.recipe = r;
        d.forEachPosition(pos -> {
            int b = blocks[pos.x][pos.y][pos.z];
            if(b>=0){
                if(templates[b]==null)throw new IllegalArgumentException("Distiller block missing from export configuration: "+configuration.blockDefinition[b]);
                d.setBlock(pos, new Block(container, pos, templates[b]));
            }
        });
        d.buildDefaultCasing();
        return d;
    }
    @Override
    public int getDimension(int id){return dims[id]+2;}
    @Override
    public Image getBlockTexture(int x, int y, int z){
        int b = blocks[x][y][z];
        return b<0?null:configuration.blockTexture[b];
    }
    @Override
    public float getCubeBounds(int x, int y, int z, int index){return index<3?0:1;}
    @Override
    public void clear(){
        // Casing and its required ports are fixed infrastructure for generation.
        for(int x = 1; x<=dims[0]; x++)for(int y = 1; y<=dims[1]; y++)for(int z = 1; z<=dims[2]; z++)blocks[x][y][z] = -1;
        calculate();
    }
    public void setLayer(int y, int block){
        for(int x = 1; x<=dims[0]; x++)for(int z = 1; z<=dims[2]; z++)blocks[x][y][z] = block;
    }
}
