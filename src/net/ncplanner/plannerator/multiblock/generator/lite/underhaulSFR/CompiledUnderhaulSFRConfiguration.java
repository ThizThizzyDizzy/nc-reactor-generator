package net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.generator.lite.CompiledConfiguration;
import net.ncplanner.plannerator.multiblock.generator.lite.CompiledPlacementRule;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.ActiveCoolerRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule;
public class CompiledUnderhaulSFRConfiguration implements CompiledConfiguration{
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public float moderatorExtraPower;
    public float moderatorExtraHeat;
    public int activeCoolerRate;
    private final ArrayList<Fuel> rawFuels = new ArrayList<>();
    private final ArrayList<NCPFElement> rawBlocks = new ArrayList<>();
    private final ArrayList<BlockAndRecipe> rawBlocksWithRecipes = new ArrayList<>();
    public NCPFElementDefinition[] fuelDefinition;
    public String[] fuelDisplayName;
    public float[] fuelPower;
    public float[] fuelHeat;
    public int[] fuelTime;
    public Image[] fuelTexture;
    public Image[] fuelDisplayTexture;
    public NCPFElementDefinition[] blockDefinition;
    public String[] blockDisplayName;
    public int[] blockCooling;
    public boolean[] blockFuelCell;
    public boolean[] blockModerator;
    public NCPFElementDefinition[] blockActive;
    public Image[] blockTexture;
    public Image[] blockDisplayTexture;
    public String[] blockType;
    public CompiledPlacementRule[][] blockPlacementRules;
    public int[] coolerIndicies;
    public int[] fuelCellIndicies;
    public int[] moderatorIndicies;
    public int[][] coolerCalculationStepIndicies;
    public boolean hasRecursiveRules;
    public static CompiledUnderhaulSFRConfiguration compile(UnderhaulSFRConfiguration config){
        CompiledUnderhaulSFRConfiguration compiled = new CompiledUnderhaulSFRConfiguration(config.settings.minSize, config.settings.maxSize, config.settings.neutronReach, config.settings.moderatorExtraPower, config.settings.moderatorExtraHeat, config.settings.activeCoolerRate);
        for(Fuel fuel : config.fuels){
            compiled.addFuel(fuel);
        }
        for(BlockElement block : config.blocks){
            compiled.addBlock(block);
        }
        compiled.compile();
        return compiled;
    }
    public CompiledUnderhaulSFRConfiguration(int minSize, int maxSize, int neutronReach, float moderatorExtraPower, float moderatorExtraHeat, int activeCoolerRate){
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.neutronReach = neutronReach;
        this.moderatorExtraPower = moderatorExtraPower;
        this.moderatorExtraHeat = moderatorExtraHeat;
        this.activeCoolerRate = activeCoolerRate;
    }
    private void addFuel(Fuel fuel){
        rawFuels.add(fuel);
    }
    private void addBlock(BlockElement block){
        if(block.casing!=null||block.controller!=null)return;
        if(block.activeCooler!=null){
            for(ActiveCoolerRecipe recipe : block.activeCoolerRecipes)rawBlocksWithRecipes.add(new BlockAndRecipe(block, recipe));
        }else{
            rawBlocksWithRecipes.add(new BlockAndRecipe(block, null));
            rawBlocks.add(block);
        }
    }
    public void compile(){
        fuelDefinition = new NCPFElementDefinition[rawFuels.size()];
        fuelDisplayName = new String[rawFuels.size()];
        fuelPower = new float[rawFuels.size()];
        fuelHeat = new float[rawFuels.size()];
        fuelTime = new int[rawFuels.size()];
        fuelTexture = new Image[rawFuels.size()];
        fuelDisplayTexture = new Image[rawFuels.size()];
        for(int i = 0; i<rawFuels.size(); i++){
            Fuel fuel = rawFuels.get(i);
            fuelDefinition[i] = fuel.definition;
            fuelDisplayName[i] = fuel.getDisplayName();
            fuelPower[i] = fuel.stats.power;
            fuelHeat[i] = fuel.stats.heat;
            fuelTime[i] = fuel.stats.time;
            fuelTexture[i] = fuel.getTexture();
            fuelDisplayTexture[i] = fuel.getDisplayTexture();
        }
        rawFuels.clear();
        blockDefinition = new NCPFElementDefinition[rawBlocksWithRecipes.size()];
        blockDisplayName = new String[rawBlocksWithRecipes.size()];
        blockCooling = new int[rawBlocksWithRecipes.size()];
        blockFuelCell = new boolean[rawBlocksWithRecipes.size()];
        blockModerator = new boolean[rawBlocksWithRecipes.size()];
        blockActive = new NCPFElementDefinition[rawBlocksWithRecipes.size()];
        blockTexture = new Image[rawBlocksWithRecipes.size()];
        blockDisplayTexture = new Image[rawBlocksWithRecipes.size()];
        blockType = new String[rawBlocksWithRecipes.size()];
        blockPlacementRules = new CompiledPlacementRule[rawBlocksWithRecipes.size()][];
        int numCoolers = 0, numCells = 0, numModerators = 0;
        for(int i = 0; i<rawBlocksWithRecipes.size(); i++){
            BlockAndRecipe raw = rawBlocksWithRecipes.get(i);
            BlockElement block = raw.block;
            ActiveCoolerRecipe recipe = raw.recipe;
            if(block.cooler!=null||recipe!=null)numCoolers++;
            if(block.fuelCell!=null)numCells++;
            if(block.moderator!=null)numModerators++;
            blockDefinition[i] = block.definition;
            blockDisplayName[i] = raw.getDisplayName();
            blockCooling[i] = recipe!=null?recipe.stats.cooling*activeCoolerRate/20:(block.cooler==null?0:block.cooler.cooling);
            blockFuelCell[i] = block.fuelCell!=null;
            blockModerator[i] = block.moderator!=null;
            blockActive[i] = recipe==null?null:recipe.definition;
            blockTexture[i] = block.texture.texture;
            blockDisplayTexture[i] = block.texture.displayTexture;
            String type = null;
            for(NCPFModule module : block.modules.modules.values()){
                if(module instanceof BlockFunctionModule)type = ((BlockFunctionModule)module).name;
            }
            blockType[i] = type;
            List<NCPFPlacementRule> rules = recipe==null?(block.cooler==null?null:block.cooler.rules):recipe.stats.rules;
            blockPlacementRules[i] = new CompiledPlacementRule[rules==null?0:rules.size()];
            for(int j = 0; j<blockPlacementRules[i].length; j++){
                NCPFPlacementRule rule = rules.get(j);
                blockPlacementRules[i][j] = CompiledPlacementRule.compile(rule, rawBlocks, CasingModule::new);
            }
        }
        for(int i = 0; i<blockDisplayTexture.length; i++){
            if(blockDisplayTexture[i]==null)blockDisplayTexture[i] = blockTexture[i];
        }
        for(int i = 0; i<fuelDisplayTexture.length; i++){
            if(fuelDisplayTexture[i]==null)fuelDisplayTexture[i] = fuelTexture[i];
        }
        rawBlocksWithRecipes.clear();
        coolerIndicies = new int[numCoolers];
        fuelCellIndicies = new int[numCells];
        moderatorIndicies = new int[numModerators];
        int coolerIndex = 0;
        int fuelCellIndex = 0;
        int moderatorIndex = 0;
        for(int i = 0; i<blockDefinition.length; i++){
            if(blockCooling[i]!=0){
                coolerIndicies[coolerIndex] = i;
                coolerIndex++;
            }
            if(blockFuelCell[i]){
                fuelCellIndicies[fuelCellIndex] = i;
                fuelCellIndex++;
            }
            if(blockModerator[i]){
                moderatorIndicies[moderatorIndex] = i;
                moderatorIndex++;
            }
        }
        ArrayList<Integer> remainingCoolers = new ArrayList<>();
        for(int i : coolerIndicies)remainingCoolers.add(i);
        ArrayList<Integer> firstLayer = new ArrayList<>();
        for(Iterator<Integer> it = remainingCoolers.iterator(); it.hasNext();){
            int i = it.next();
            if(!usesCooler(blockPlacementRules[i])){
                firstLayer.add(i);
                it.remove();
            }
        }
        ArrayList<ArrayList<Integer>> coolerRuleIndicies = new ArrayList<>();
        ArrayList<Integer> previousLayer = firstLayer;
        ArrayList<Integer> nextLayer;
        do{
            coolerRuleIndicies.add(previousLayer);
            nextLayer = new ArrayList<>();
            for(Iterator<Integer> it = remainingCoolers.iterator(); it.hasNext();){
                int i = it.next();
                if(onlyContains(blockPlacementRules[i], previousLayer)){
                    nextLayer.add(i);
                    it.remove();
                }
            }
            previousLayer = nextLayer;
        }while(!nextLayer.isEmpty());
        if(!remainingCoolers.isEmpty()){
            hasRecursiveRules = true;
            coolerRuleIndicies.add(remainingCoolers);
        }
        coolerCalculationStepIndicies = new int[coolerRuleIndicies.size()][];
        for(int i = 0; i<coolerRuleIndicies.size(); i++){
            ArrayList<Integer> indicies = coolerRuleIndicies.get(i);
            coolerCalculationStepIndicies[i] = new int[indicies.size()];
            for(int j = 0; j<coolerCalculationStepIndicies[i].length; j++){
                coolerCalculationStepIndicies[i][j] = indicies.get(j);
            }
        }
    }
    private boolean usesCooler(CompiledPlacementRule[] rules){
        if(rules==null)return false;
        for(CompiledPlacementRule rule : rules){
            if(usesCooler(rule.rules))return true;
            if(rule.block==-1)continue;
            if(rule.blockType==null&&blockCooling[rule.block]!=0)return true;
        }
        return false;
    }
    private boolean onlyContains(CompiledPlacementRule[] rules, ArrayList<Integer> blocks){
        if(rules==null)return true;
        for(CompiledPlacementRule rule : rules){
            if(!onlyContains(rule.rules, blocks))return false;
            if(rule.blockType==null&&(rule.block!=-1&&blockCooling[rule.block]!=0)&&!blocks.contains(rule.block))return false;
        }
        return true;
    }
    private static class BlockAndRecipe implements Supplier<BlockElement>{
        private final BlockElement block;
        private final ActiveCoolerRecipe recipe;
        public BlockAndRecipe(BlockElement block, ActiveCoolerRecipe recipe){
            this.block = block;
            this.recipe = recipe;
        }
        @Override
        public BlockElement get(){
            return block;
        }
        public String getDisplayName(){
            return block.getDisplayName()+(recipe!=null?" ("+recipe.getDisplayName()+")":"");
        }
    }
}