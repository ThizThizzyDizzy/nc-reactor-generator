package multiblock.configuration.overhaul.fusion;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import multiblock.Multiblock;
import multiblock.configuration.AddonConfiguration;
import multiblock.configuration.Configuration;
import multiblock.configuration.PartialConfiguration;
import multiblock.configuration.overhaul.OverhaulConfiguration;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import planner.exception.MissingConfigurationEntryException;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class FusionConfiguration{
    public ArrayList<Block> allBlocks = new ArrayList<>();
    public ArrayList<Recipe> allRecipes = new ArrayList<>();
    public ArrayList<CoolantRecipe> allCoolantRecipes = new ArrayList<>();
    /**
     * @deprecated You should probably be using allBlocks
     */
    @Deprecated
    public ArrayList<Block> blocks = new ArrayList<>();
    /**
     * @deprecated You should probably be using allRecipes
     */
    @Deprecated
    public ArrayList<Recipe> recipes = new ArrayList<>();
    /**
     * @deprecated You should probably be using allCoolantRecipes
     */
    @Deprecated
    public ArrayList<CoolantRecipe> coolantRecipes = new ArrayList<>();
    public int minInnerRadius, maxInnerRadius, minCoreSize, maxCoreSize, minToroidWidth, maxToroidWidth, minLiningThickness, maxLiningThickness;
    public float sparsityPenaltyMult;
    public float sparsityPenaltyThreshold;
    public int coolingEfficiencyLeniency;
    public String[] getAllBlocksStringList(){
        String[] strs = new String[allBlocks.size()];
        for(int i = 0; i<strs.length; i++){
            strs[i] = allBlocks.get(i).name;
        }
        return strs;
    }
    public Config save(Configuration parent, boolean partial){
        Config config = Config.newConfig();
        if(parent==null&&!partial){
            config.set("minInnerRadius", minInnerRadius);
            config.set("maxInnerRadius", maxInnerRadius);
            config.set("minCoreSize", minCoreSize);
            config.set("maxCoreSize", maxCoreSize);
            config.set("minToroidWidth", minToroidWidth);
            config.set("maxToroidWidth", maxToroidWidth);
            config.set("minLiningThickness", minLiningThickness);
            config.set("maxLiningThickness", maxLiningThickness);
            config.set("coolingEfficiencyLeniency", coolingEfficiencyLeniency);
            config.set("sparsityPenaltyMult", sparsityPenaltyMult);
            config.set("sparsityPenaltyThreshold", sparsityPenaltyThreshold);
        }
        ConfigList blocks = new ConfigList();
        for(Block block : this.blocks){
            blocks.add(block.save(parent, this, partial));
        }
        config.set("blocks", blocks);
        ConfigList recipes = new ConfigList();
        for(Recipe recipe : this.recipes){
            recipes.add(recipe.save(partial));
        }
        config.set("recipes", recipes);
        ConfigList coolantRecipes = new ConfigList();
        for(CoolantRecipe coolantRecipe : this.coolantRecipes){
            coolantRecipes.add(coolantRecipe.save(partial));
        }
        config.set("coolantRecipes", coolantRecipes);
        return config;
    }
    public void apply(FusionConfiguration partial, ArrayList<Multiblock> multiblocks, PartialConfiguration parent){
        Set<Block> usedBlocks = new HashSet<>();
        Set<Recipe> usedRecipes = new HashSet<>();
        Set<CoolantRecipe> usedCoolantRecipes = new HashSet<>();
        for(Multiblock mb : multiblocks){
            if(mb instanceof OverhaulFusionReactor){
                for(multiblock.overhaul.fusion.Block b : ((OverhaulFusionReactor)mb).getBlocks()){
                    usedBlocks.add(b.template);
                }
                usedRecipes.add(((OverhaulFusionReactor)mb).recipe);
                usedCoolantRecipes.add(((OverhaulFusionReactor)mb).coolantRecipe);
            }
        }
        partial.blocks.addAll(usedBlocks);
        partial.recipes.addAll(usedRecipes);
        partial.coolantRecipes.addAll(usedCoolantRecipes);
        parent.overhaul.fusion.allBlocks.addAll(usedBlocks);
        parent.overhaul.fusion.allRecipes.addAll(usedRecipes);
        parent.overhaul.fusion.allCoolantRecipes.addAll(usedCoolantRecipes);
    }
    public void apply(AddonConfiguration addon, Configuration parent){
        Set<Block> usedBlocks = new HashSet<>();
        for(Block b : blocks){
            usedBlocks.addAll(getAllUsedBlocks(b));
            usedBlocks.removeAll(blocks);
        }
        //parent blocks
        ArrayList<Block> theBlocks = new ArrayList<>();
        for(Block b : parent.overhaul.fusion.blocks){
            if(usedBlocks.contains(b)){
                theBlocks.add(b);
            }
        }
        addon.overhaul.fusion.allBlocks.addAll(theBlocks);
        addon.overhaul.fusion.blocks.addAll(theBlocks);
        //self blocks
        addon.self.overhaul.fusion.blocks.addAll(blocks);
        addon.overhaul.fusion.allBlocks.addAll(blocks);
        //addon blocks
        for(Configuration addn : parent.addons){
            theBlocks = new ArrayList<>();
            if(addn.overhaul!=null&&addn.overhaul.fusion!=null){
                for(Block b : addn.overhaul.fusion.blocks){
                    if(usedBlocks.contains(b)){
                        theBlocks.add(b);
                    }
                }
            }
            addon.overhaul.fusion.allBlocks.addAll(theBlocks);
            if(!theBlocks.isEmpty()){
                boolean foundMatch = false;
                for(Configuration c : addon.addons){
                    if(c.overhaulNameMatches(addn)){
                        foundMatch = true;
                        c.overhaul.fusion.blocks.addAll(theBlocks);
                    }
                }
                if(!foundMatch){
                    Configuration c = new PartialConfiguration(addn.name, addn.overhaulVersion, addn.overhaulVersion);
                    addon.addons.add(c);
                    c.addon = true;
                    c.overhaul = new OverhaulConfiguration();
                    c.overhaul.fusion = new FusionConfiguration();
                    c.overhaul.fusion.blocks.addAll(theBlocks);
                }
            }
        }
        addon.self.overhaul.fusion.recipes.addAll(recipes);
        parent.overhaul.fusion.allRecipes.addAll(recipes);
        addon.self.overhaul.fusion.coolantRecipes.addAll(coolantRecipes);
        parent.overhaul.fusion.allCoolantRecipes.addAll(coolantRecipes);
    }
    public Block convert(Block template) throws MissingConfigurationEntryException{
        if(template==null)return null;
        for(Block block : allBlocks){
            for(String name : block.getLegacyNames()){
                if(name.equals(template.name))return block;
            }
        }
        for(Block block : blocks){
            for(String name : block.getLegacyNames()){
                if(name.equals(template.name))return block;
            }
        }
        throw new MissingConfigurationEntryException("Failed to find match for block "+template.name+"!");
    }
    public Recipe convert(Recipe template) throws MissingConfigurationEntryException{
        if(template==null)return null;
        for(Recipe recipe : allRecipes){
            for(String name : recipe.getLegacyNames()){
                if(name.equals(template.inputName))return recipe;
            }
        }
        for(Recipe recipe : recipes){
            for(String name : recipe.getLegacyNames()){
                if(name.equals(template.inputName))return recipe;
            }
        }
        throw new MissingConfigurationEntryException("Failed to find match for recipe "+template.inputName+"!");
    }
    public CoolantRecipe convert(CoolantRecipe template){
        if(template==null)return null;
        for(CoolantRecipe coolantRecipe : allCoolantRecipes){
            for(String name : coolantRecipe.getLegacyNames()){
                if(name.equals(template.inputName))return coolantRecipe;
            }
        }
        for(CoolantRecipe coolantRecipe : coolantRecipes){
            for(String name : coolantRecipe.getLegacyNames()){
                if(name.equals(template.inputName))return coolantRecipe;
            }
        }
        throw new IllegalArgumentException("Failed to find match for coolant recipe "+template.inputName+"!");
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof FusionConfiguration){
            FusionConfiguration fc = (FusionConfiguration)obj;
            return Objects.equals(fc.blocks, blocks)
                    &&Objects.equals(fc.recipes, recipes)
                    &&Objects.equals(fc.coolantRecipes, coolantRecipes)
                    &&minInnerRadius==fc.minInnerRadius
                    &&maxInnerRadius==fc.maxInnerRadius
                    &&minCoreSize==fc.minCoreSize
                    &&maxCoreSize==fc.maxCoreSize
                    &&minToroidWidth==fc.minToroidWidth
                    &&maxToroidWidth==fc.maxToroidWidth
                    &&minLiningThickness==fc.minLiningThickness
                    &&maxLiningThickness==fc.maxLiningThickness
                    &&sparsityPenaltyMult==fc.sparsityPenaltyMult
                    &&sparsityPenaltyThreshold==fc.sparsityPenaltyThreshold
                    &&coolingEfficiencyLeniency==fc.coolingEfficiencyLeniency;
        }
        return false;
    }
    private ArrayList<Block> getAllUsedBlocks(RuleContainer container){
        ArrayList<Block> used = new ArrayList<>();
        for(PlacementRule rule : container.rules){
            used.addAll(getAllUsedBlocks(rule));
            if(rule.block!=null)used.add(rule.block);
        }
        return used;
    }
    private ArrayList<PlacementRule> getAllSubRules(RuleContainer container){
        ArrayList<PlacementRule> rules = new ArrayList<>();
        for(PlacementRule rule : container.rules){
            rules.addAll(getAllSubRules(rule));
            rules.add(rule);
        }
        return rules;
    }
    public void convertAddon(AddonConfiguration parent, Configuration convertTo) throws MissingConfigurationEntryException{
        for(Block block : blocks){
            for(PlacementRule rule : getAllSubRules(block)){
                if(rule.block==null)continue;
                if(parent.overhaul!=null&&parent.overhaul.fusion!=null&&parent.overhaul.fusion.blocks.contains(rule.block)){
                    rule.block = convertTo.overhaul.fusion.convert(rule.block);
                }else if(blocks.contains(rule.block)){
                    //do nothing :)
                }else{
                    //in sub-addon, find and convert
                    boolean found = false;
                    for(Configuration addon : parent.addons){
                        if(addon.overhaul!=null&&addon.overhaul.fusion!=null){
                            if(addon.overhaul.fusion.blocks.contains(rule.block)){
                                rule.block = convertTo.findMatchingAddon(addon).overhaul.fusion.convert(rule.block);
                                found = true;
                            }
                        }
                    }
                    if(!found)throw new IllegalArgumentException("Could not convert block "+block.name+"!");
                }
            }
        }
    }
}