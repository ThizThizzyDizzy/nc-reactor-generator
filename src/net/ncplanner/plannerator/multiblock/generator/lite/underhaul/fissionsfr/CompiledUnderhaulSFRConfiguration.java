package net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.Iterator;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.PlacementRule;
import net.ncplanner.plannerator.multiblock.generator.lite.CompiledPlacementRule;
public class CompiledUnderhaulSFRConfiguration{
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public float moderatorExtraPower;
    public float moderatorExtraHeat;
    public int activeCoolerRate;
    private final ArrayList<Fuel> rawFuels = new ArrayList<>();
    private final ArrayList<Block> rawBlocks = new ArrayList<>();
    public String[] fuelName;
    public String[] fuelDisplayName;
    public String[][] fuelLegacyNames;
    public float[] fuelPower;
    public float[] fuelHeat;
    public int[] fuelTime;
    public Image[] fuelTexture;
    public Image[] fuelDisplayTexture;
    public String[] blockName;
    public String[] blockDisplayName;
    public String[][] blockLegacyNames;
    public int[] blockCooling;
    public boolean[] blockFuelCell;
    public boolean[] blockModerator;
    public boolean[] blockCasing;
    public boolean[] blockController;
    public String[] blockActive;
    public Image[] blockTexture;
    public Image[] blockDisplayTexture;
    public CompiledUnderhaulSFRPlacementRule[][] blockPlacementRules;
    public int[] coolerIndicies;
    public int[] fuelCellIndicies;
    public int[] moderatorIndicies;
    public int[] casingIndicies;
    public int[] controllerIndicies;
    public int[][] coolerCalculationStepIndicies;
    public boolean hasRecursiveRules;
    public static CompiledUnderhaulSFRConfiguration compile(FissionSFRConfiguration config){
        CompiledUnderhaulSFRConfiguration compiled = new CompiledUnderhaulSFRConfiguration(config.minSize, config.maxSize, config.neutronReach, config.moderatorExtraPower, config.moderatorExtraHeat, config.activeCoolerRate);
        for(Fuel fuel : config.allFuels){
            compiled.addFuel(fuel);
        }
        for(Block block : config.allBlocks){
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
    public void addFuel(Fuel fuel){
        rawFuels.add(fuel);
    }
    private void addBlock(Block block){
        if(block.casing||block.controller)return;
        rawBlocks.add(block);
    }
    public void compile(){
        fuelName = new String[rawFuels.size()];
        fuelDisplayName = new String[rawFuels.size()];
        fuelLegacyNames = new String[rawFuels.size()][];
        fuelPower = new float[rawFuels.size()];
        fuelHeat = new float[rawFuels.size()];
        fuelTime = new int[rawFuels.size()];
        fuelTexture = new Image[rawFuels.size()];
        fuelDisplayTexture = new Image[rawFuels.size()];
        for(int i = 0; i<rawFuels.size(); i++){
            Fuel fuel = rawFuels.get(i);
            fuelName[i] = fuel.name;
            fuelDisplayName[i] = fuel.displayName;
            fuelLegacyNames[i] = fuel.legacyNames.toArray(new String[fuel.legacyNames.size()]);
            fuelPower[i] = fuel.power;
            fuelHeat[i] = fuel.heat;
            fuelTime[i] = fuel.time;
            fuelTexture[i] = fuel.texture;
            fuelDisplayTexture[i] = fuel.displayTexture;
        }
        rawFuels.clear();
        blockName = new String[rawBlocks.size()];
        blockDisplayName = new String[rawBlocks.size()];
        blockLegacyNames = new String[rawBlocks.size()][];
        blockCooling = new int[rawBlocks.size()];
        blockFuelCell = new boolean[rawBlocks.size()];
        blockModerator = new boolean[rawBlocks.size()];
        blockCasing = new boolean[rawBlocks.size()];
        blockController = new boolean[rawBlocks.size()];
        blockActive = new String[rawBlocks.size()];
        blockTexture = new Image[rawBlocks.size()];
        blockDisplayTexture = new Image[rawBlocks.size()];
        blockPlacementRules = new CompiledUnderhaulSFRPlacementRule[rawBlocks.size()][];
        int numCoolers = 0, numCells = 0, numModerators = 0, numCasings = 0, numControllers = 0;
        for(int i = 0; i<rawBlocks.size(); i++){
            Block block = rawBlocks.get(i);
            if(block.cooling!=0)numCoolers++;
            if(block.fuelCell)numCells++;
            if(block.moderator)numModerators++;
            if(block.casing)numCasings++;
            if(block.controller)numControllers++;
            blockName[i] = block.name;
            blockDisplayName[i] = block.displayName;
            blockLegacyNames[i] = block.legacyNames.toArray(new String[block.legacyNames.size()]);
            blockCooling[i] = block.active!=null?block.cooling*activeCoolerRate/20:block.cooling;
            blockFuelCell[i] = block.fuelCell;
            blockModerator[i] = block.moderator;
            blockCasing[i] = block.casing;
            blockController[i] = block.controller;
            blockActive[i] = block.active;
            blockTexture[i] = block.texture;
            blockDisplayTexture[i] = block.displayTexture;
            blockPlacementRules[i] = new CompiledUnderhaulSFRPlacementRule[block.rules.size()];
            for(int j = 0; j<block.rules.size(); j++){
                AbstractPlacementRule<PlacementRule.BlockType, Block> rule = block.rules.get(j);
                blockPlacementRules[i][j] = CompiledUnderhaulSFRPlacementRule.compileUnderhaulSFR(rule, rawBlocks);
            }
        }
        rawBlocks.clear();
        coolerIndicies = new int[numCoolers];
        fuelCellIndicies = new int[numCells];
        moderatorIndicies = new int[numModerators];
        casingIndicies = new int[numCasings];
        controllerIndicies = new int[numControllers];
        int coolerIndex = 0;
        int fuelCellIndex = 0;
        int moderatorIndex = 0;
        int casingIndex = 0;
        int controllerIndex = 0;
        for(int i = 0; i<blockName.length; i++){
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
            if(blockCasing[i]){
                casingIndicies[casingIndex] = i;
                casingIndex++;
            }
            if(blockController[i]){
                controllerIndicies[controllerIndex] = i;
                controllerIndex++;
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
    public boolean matches(Configuration config){
        return config!=null
                &&config.underhaul!=null
                &&config.underhaul.fissionSFR!=null
                &&config.underhaul.fissionSFR.allFuels.size()==fuelName.length
                &&config.underhaul.fissionSFR.allBlocks.size()==blockName.length;
    }
    private boolean usesCooler(CompiledPlacementRule<PlacementRule.BlockType, Block>[] rules){
        if(rules==null)return false;
        for(CompiledPlacementRule<PlacementRule.BlockType, Block> rule : rules){
            if(usesCooler(rule.rules))return true;
            if(rule.isSpecificBlock&&blockCooling[rule.block]!=0)return true;
        }
        return false;
    }
    private boolean onlyContains(CompiledPlacementRule<PlacementRule.BlockType, Block>[] rules, ArrayList<Integer> blocks){
        if(rules==null)return true;
        for(CompiledPlacementRule<PlacementRule.BlockType, Block> rule : rules){
            if(!onlyContains(rule.rules, blocks))return false;
            if(rule.isSpecificBlock&&blockCooling[rule.block]!=0&&!blocks.contains(rule.block))return false;
        }
        return true;
    }
}