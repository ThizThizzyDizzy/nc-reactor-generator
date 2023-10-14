package net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.generator.lite.CompiledPlacementRule;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CasingModule;
public class CompiledOverhaulSFRConfiguration{
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public int coolingEfficiencyLeniency;
    public float sparsityPenaltyMultiplier;
    public float sparsityPenaltyThreshold;
    private final ArrayList<CoolantRecipe> rawCoolantRecipes = new ArrayList<>();
    private final ArrayList<NCPFElement> rawBlocks = new ArrayList<>();
    private final ArrayList<BlockAndRecipe> rawBlocksWithRecipes = new ArrayList<>();
    public NCPFElementDefinition[] coolantRecipeDefinition;
    public String[] coolantRecipeDisplayName;
    public int[] coolantRecipeHeat;
    public float[] coolantRecipeOutputRatio;
    public Image[] coolantRecipeTexture;
    public Image[] coolantRecipeDisplayTexture;
    public NCPFElementDefinition[] blockDefinition;
    public String[] blockDisplayName;
    public boolean[] blockConductor;
    public boolean[] blockFuelCell;
    public boolean[] blockIrradiator;
    public boolean[] blockReflector;
    public boolean[] blockModerator;
    public boolean[] blockShield;
    public boolean[] blockHeatsink;
    public boolean[] blockSource;
    public boolean[] losTest;
    //stats
    public float[] blockEfficiency;
    public int[] blockHeat;
    public int[] blockTime;
    public int[] blockCriticality;
    public float[] blockHeatMult;//irradiator heat, because it's a float
    public boolean[] blockSelfPriming;
    public float[] blockReflectivity;
    public int[] blockFlux;
    public int[] blockHeatPerFlux;//shield
    public int[] blockCooling;
    public NCPFElementDefinition[] blockRecipe;
    public Image[] blockTexture;
    public Image[] blockDisplayTexture;
    public String[] blockType;
    public CompiledPlacementRule[][] blockPlacementRules;
    public int[] conductorIndicies;
    public int[] fuelCellIndicies;
    public int[] irradiatorIndicies;
    public int[] reflectorIndicies;
    public int[] moderatorIndicies;
    public int[] shieldIndicies;
    public int[] heatsinkIndicies;
    public int[] sourceIndicies;
    public int[][] heatsinkCalculationStepIndicies;
    public boolean hasRecursiveRules;
    public static CompiledOverhaulSFRConfiguration compile(OverhaulSFRConfiguration config){
        CompiledOverhaulSFRConfiguration compiled = new CompiledOverhaulSFRConfiguration(config.settings.minSize, config.settings.maxSize, config.settings.neutronReach, config.settings.coolingEfficiencyLeniency, config.settings.sparsityPenaltyMultiplier, config.settings.sparsityPenaltyThreshold);
        for(CoolantRecipe recipe : config.coolantRecipes){
            compiled.addCoolantRecipe(recipe);
        }
        for(BlockElement block : config.blocks){
            compiled.addBlock(block);
        }
        compiled.compile();
        return compiled;
    }
    public CompiledOverhaulSFRConfiguration(int minSize, int maxSize, int neutronReach, int coolingEfficiencyLeniency, float sparsityPenaltyMultiplier, float sparsityPenaltyThreshold){
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.neutronReach = neutronReach;
        this.coolingEfficiencyLeniency = coolingEfficiencyLeniency;
        this.sparsityPenaltyMultiplier = sparsityPenaltyMultiplier;
        this.sparsityPenaltyThreshold = sparsityPenaltyThreshold;
    }
    private void addCoolantRecipe(CoolantRecipe recipe){
        rawCoolantRecipes.add(recipe);
    }
    private void addBlock(BlockElement block){
        if(block.casing!=null||block.controller!=null)return;
        rawBlocks.add(block);
        if(block.fuelCell!=null)for(Fuel fuel : block.fuels)rawBlocksWithRecipes.add(new BlockAndFuel(block, fuel));
        else if(block.irradiator!=null)for(IrradiatorRecipe recipe : block.irradiatorRecipes)rawBlocksWithRecipes.add(new BlockAndIrrecipe(block, recipe));
        else rawBlocksWithRecipes.add(new BlockAndRecipe(block, null));
    }
    private void compile(){
        coolantRecipeDefinition = new NCPFElementDefinition[rawCoolantRecipes.size()];
        coolantRecipeDisplayName = new String[rawCoolantRecipes.size()];
        coolantRecipeHeat = new int[rawCoolantRecipes.size()];
        coolantRecipeOutputRatio = new float[rawCoolantRecipes.size()];
        coolantRecipeTexture = new Image[rawCoolantRecipes.size()];
        coolantRecipeDisplayTexture = new Image[rawCoolantRecipes.size()];
        for(int i = 0; i<rawCoolantRecipes.size(); i++){
            CoolantRecipe recipe = rawCoolantRecipes.get(i);
            coolantRecipeDefinition[i] = recipe.definition;
            coolantRecipeDisplayName[i] = recipe.getDisplayName();
            coolantRecipeHeat[i] = recipe.stats.heat;
            coolantRecipeOutputRatio[i] = recipe.stats.outputRatio;
            coolantRecipeTexture[i] = recipe.getTexture();
            coolantRecipeDisplayTexture[i] = recipe.getDisplayTexture();
        }
        rawCoolantRecipes.clear();
        blockDefinition = new NCPFElementDefinition[rawBlocksWithRecipes.size()];
        blockDisplayName = new String[rawBlocksWithRecipes.size()];
        blockConductor = new boolean[rawBlocksWithRecipes.size()];
        blockFuelCell = new boolean[rawBlocksWithRecipes.size()];
        blockIrradiator = new boolean[rawBlocksWithRecipes.size()];
        blockReflector = new boolean[rawBlocksWithRecipes.size()];
        blockModerator = new boolean[rawBlocksWithRecipes.size()];
        blockShield = new boolean[rawBlocksWithRecipes.size()];
        blockHeatsink = new boolean[rawBlocksWithRecipes.size()];
        blockSource = new boolean[rawBlocksWithRecipes.size()];
        losTest = new boolean[rawBlocksWithRecipes.size()];
        blockEfficiency = new float[rawBlocksWithRecipes.size()];
        blockHeat = new int[rawBlocksWithRecipes.size()];
        blockTime = new int[rawBlocksWithRecipes.size()];
        blockCriticality = new int[rawBlocksWithRecipes.size()];
        blockHeatMult = new float[rawBlocksWithRecipes.size()];
        blockSelfPriming = new boolean[rawBlocksWithRecipes.size()];
        blockReflectivity = new float[rawBlocksWithRecipes.size()];
        blockFlux = new int[rawBlocksWithRecipes.size()];
        blockHeatPerFlux = new int[rawBlocksWithRecipes.size()];
        blockCooling = new int[rawBlocksWithRecipes.size()];
        blockRecipe = new NCPFElementDefinition[rawBlocksWithRecipes.size()];
        blockTexture = new Image[rawBlocksWithRecipes.size()];
        blockDisplayTexture = new Image[rawBlocksWithRecipes.size()];
        blockType = new String[rawBlocksWithRecipes.size()];
        blockPlacementRules = new CompiledPlacementRule[rawBlocksWithRecipes.size()][];
        int numConductors = 0, numCells = 0, numIrradiators = 0, numReflectors = 0, numModerators = 0, numShields = 0, numHeatsinks = 0, numSources = 0;
        for(int i = 0; i<rawBlocksWithRecipes.size(); i++){
            BlockAndRecipe raw = rawBlocksWithRecipes.get(i);
            BlockElement block = raw.block;
            blockDefinition[i] = block.definition;
            blockDisplayName[i] = block.names.displayName;
            losTest[i] = true;
            if(block.conductor!=null){
                blockConductor[i] = true;
                numConductors++;
            }
            if(block.fuelCell!=null){
                numCells++;
                Fuel fuel = ((BlockAndFuel)raw).recipe;
                blockRecipe[i] = fuel.definition;
                blockFuelCell[i] = true;
                blockEfficiency[i] = fuel.stats.efficiency;
                blockHeat[i] = fuel.stats.heat;
                blockTime[i] = fuel.stats.time;
                blockCriticality[i] = fuel.stats.criticality;
                blockSelfPriming[i] = fuel.stats.selfPriming;
                losTest[i] = false;
            }
            if(block.irradiator!=null){
                numIrradiators++;
                IrradiatorRecipe irrecipe = ((BlockAndIrrecipe)raw).recipe;
                blockRecipe[i] = irrecipe.definition;
                blockIrradiator[i] = true;
                blockEfficiency[i] = irrecipe.stats.efficiency;
                blockHeatMult[i] = irrecipe.stats.heat;
                losTest[i] = false;
            }
            if(block.reflector!=null){
                numReflectors++;
                blockReflector[i] = true;
                blockEfficiency[i] = block.reflector.efficiency;
                blockReflectivity[i] = block.reflector.reflectivity;
                losTest[i] = false;
            }
            if(block.moderator!=null){
                numModerators++;
                blockModerator[i] = true;
                blockEfficiency[i] = block.moderator.efficiency;
                blockFlux[i] = block.moderator.flux;
            }
            if(block.neutronShield!=null){
                numShields++;
                blockShield[i] = true;
                blockEfficiency[i] = block.neutronShield.efficiency;
                blockHeatPerFlux[i] = block.neutronShield.heatPerFlux;
            }
            if(block.heatsink!=null){
                numHeatsinks++;
                blockHeatsink[i] = true;
                blockCooling[i] = block.heatsink.cooling;
                List<NCPFPlacementRule> rules = block.heatsink.rules;
                blockPlacementRules[i] = new CompiledPlacementRule[rules==null?0:rules.size()];
                for(int j = 0; j<blockPlacementRules[i].length; j++){
                    NCPFPlacementRule rule = rules.get(j);
                    blockPlacementRules[i][j] = CompiledPlacementRule.compile(rule, rawBlocks, CasingModule::new);
                }
            }
            if(block.neutronSource!=null){
                numSources++;
                blockSource[i] = true;
                blockEfficiency[i] = block.neutronSource.efficiency;
            }
            int idx = i;
            block.withModule(TextureModule::new, (tex)->{
                blockTexture[idx] = tex.texture;
                blockDisplayTexture[idx] = tex.displayTexture;
            });
            String type = null;
            for(NCPFModule module : block.modules.modules.values()){
                if(module instanceof BlockFunctionModule)type = ((BlockFunctionModule)module).name;
            }
            blockType[i] = type;
        }
        conductorIndicies = new int[numConductors];
        fuelCellIndicies = new int[numCells];
        irradiatorIndicies = new int[numIrradiators];
        reflectorIndicies = new int[numReflectors];
        moderatorIndicies = new int[numModerators];
        shieldIndicies = new int[numShields];
        heatsinkIndicies = new int[numHeatsinks];
        sourceIndicies = new int[numSources];
        int conductorIndex = 0;
        int fuelCellIndex = 0;
        int irradiatorIndex = 0;
        int reflectorIndex = 0;
        int moderatorIndex = 0;
        int shieldIndex = 0;
        int heatsinkIndex = 0;
        int sourceIndex = 0;
        for(int i = 0; i<blockDefinition.length; i++){
            if(blockConductor[i]){
                conductorIndicies[conductorIndex] = i;
                conductorIndex++;
            }
            if(blockFuelCell[i]){
                fuelCellIndicies[fuelCellIndex] = i;
                fuelCellIndex++;
            }
            if(blockIrradiator[i]){
                irradiatorIndicies[irradiatorIndex] = i;
                irradiatorIndex++;
            }
            if(blockReflector[i]){
                reflectorIndicies[reflectorIndex] = i;
                reflectorIndex++;
            }
            if(blockModerator[i]){
                moderatorIndicies[moderatorIndex] = i;
                moderatorIndex++;
            }
            if(blockShield[i]){
                shieldIndicies[shieldIndex] = i;
                shieldIndex++;
            }
            if(blockHeatsink[i]){
                heatsinkIndicies[heatsinkIndex] = i;
                heatsinkIndex++;
            }
            if(blockSource[i]){
                sourceIndicies[sourceIndex] = i;
                sourceIndex++;
            }
        }
        
        ArrayList<Integer> remainingHeatsinks = new ArrayList<>();
        for(int i : heatsinkIndicies)remainingHeatsinks.add(i);
        ArrayList<Integer> firstLayer = new ArrayList<>();
        for(Iterator<Integer> it = remainingHeatsinks.iterator(); it.hasNext();){
            int i = it.next();
            if(!usesHeatsink(blockPlacementRules[i])){
                firstLayer.add(i);
                it.remove();
            }
        }
        ArrayList<ArrayList<Integer>> heatsinkRuleIndicies = new ArrayList<>();
        ArrayList<Integer> previousLayer = firstLayer;
        ArrayList<Integer> nextLayer;
        do{
            heatsinkRuleIndicies.add(previousLayer);
            nextLayer = new ArrayList<>();
            for(Iterator<Integer> it = remainingHeatsinks.iterator(); it.hasNext();){
                int i = it.next();
                if(onlyContains(blockPlacementRules[i], previousLayer)){
                    nextLayer.add(i);
                    it.remove();
                }
            }
            previousLayer = nextLayer;
        }while(!nextLayer.isEmpty());
        if(!remainingHeatsinks.isEmpty()){
            hasRecursiveRules = true;
            heatsinkRuleIndicies.add(remainingHeatsinks);
        }
        heatsinkCalculationStepIndicies = new int[heatsinkRuleIndicies.size()][];
        for(int i = 0; i<heatsinkRuleIndicies.size(); i++){
            ArrayList<Integer> indicies = heatsinkRuleIndicies.get(i);
            heatsinkCalculationStepIndicies[i] = new int[indicies.size()];
            for(int j = 0; j<heatsinkCalculationStepIndicies[i].length; j++){
                heatsinkCalculationStepIndicies[i][j] = indicies.get(j);
            }
        }
    }
    private boolean usesHeatsink(CompiledPlacementRule[] rules){
        if(rules==null)return false;
        for(CompiledPlacementRule rule : rules){
            if(usesHeatsink(rule.rules))return true;
            if(rule.block==-1)continue;
            if(rule.blockType==null&&blockHeatsink[rule.block])return true;
        }
        return false;
    }
    private boolean onlyContains(CompiledPlacementRule[] rules, ArrayList<Integer> blocks){
        if(rules==null)return true;
        for(CompiledPlacementRule rule : rules){
            if(!onlyContains(rule.rules, blocks))return false;
            if(rule.blockType==null&&(rule.block!=-1&&blockHeatsink[rule.block])&&!blocks.contains(rule.block))return false;
        }
        return true;
    }
    private static class BlockAndRecipe<T extends NCPFElement>{
        public final BlockElement block;
        public final T recipe;
        public BlockAndRecipe(BlockElement block, T recipe){
            this.block = block;
            this.recipe = recipe;
        }
    }
    private static class BlockAndFuel extends BlockAndRecipe<Fuel>{
        public BlockAndFuel(BlockElement block, Fuel fuel){
            super(block, fuel);
        }
    }
    private static class BlockAndIrrecipe extends BlockAndRecipe<IrradiatorRecipe>{
        public BlockAndIrrecipe(BlockElement block, IrradiatorRecipe irrecipe){
            super(block, irrecipe);
        }
    }
}