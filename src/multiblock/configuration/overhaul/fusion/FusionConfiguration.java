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
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class FusionConfiguration{
    public ArrayList<Block> allBlocks = new ArrayList<>();
    public ArrayList<BreedingBlanketRecipe> allBreedingBlanketRecipes = new ArrayList<>();
    public ArrayList<Recipe> allRecipes = new ArrayList<>();
    public ArrayList<CoolantRecipe> allCoolantRecipes = new ArrayList<>();
    /**
     * @deprecated You should probably be using allBlocks
     */
    @Deprecated
    public ArrayList<Block> blocks = new ArrayList<>();
    /**
     * @deprecated You should probably be using allBreedingBlanketRecipes
     */
    @Deprecated
    public ArrayList<BreedingBlanketRecipe> breedingBlanketRecipes = new ArrayList<>();
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
            recipes.add(recipe.save());
        }
        config.set("recipes", recipes);
        ConfigList coolantRecipes = new ConfigList();
        for(CoolantRecipe coolantRecipe : this.coolantRecipes){
            coolantRecipes.add(coolantRecipe.save());
        }
        config.set("coolantRecipes", coolantRecipes);
        ConfigList breedingBlanketRecipes = new ConfigList();
        for(BreedingBlanketRecipe recipe : this.breedingBlanketRecipes){
            breedingBlanketRecipes.add(recipe.save());
        }
        config.set("breedingBlanketRecipes", breedingBlanketRecipes);
        return config;
    }
    public void apply(FusionConfiguration partial, ArrayList<Multiblock> multiblocks, PartialConfiguration parent){
        Set<Block> usedBlocks = new HashSet<>();
        Set<BreedingBlanketRecipe> usedBreedingBlanketRecipes = new HashSet<>();
        Set<Recipe> usedRecipes = new HashSet<>();
        Set<CoolantRecipe> usedCoolantRecipes = new HashSet<>();
        for(Multiblock mb : multiblocks){
            if(mb instanceof OverhaulFusionReactor){
                for(multiblock.overhaul.fusion.Block b : ((OverhaulFusionReactor)mb).getBlocks()){
                    usedBlocks.add(b.template);
                    if(b.breedingBlanketRecipe!=null)usedBreedingBlanketRecipes.add(b.breedingBlanketRecipe);
                }
                usedRecipes.add(((OverhaulFusionReactor)mb).recipe);
                usedCoolantRecipes.add(((OverhaulFusionReactor)mb).coolantRecipe);
            }
        }
        partial.blocks.addAll(usedBlocks);
        partial.breedingBlanketRecipes.addAll(usedBreedingBlanketRecipes);
        partial.recipes.addAll(usedRecipes);
        partial.coolantRecipes.addAll(usedCoolantRecipes);
        parent.overhaul.fusion.allBlocks.addAll(usedBlocks);
        parent.overhaul.fusion.allBreedingBlanketRecipes.addAll(usedBreedingBlanketRecipes);
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
        addon.self.overhaul.fusion.breedingBlanketRecipes.addAll(breedingBlanketRecipes);
        parent.overhaul.fusion.allBreedingBlanketRecipes.addAll(breedingBlanketRecipes);
        addon.self.overhaul.fusion.recipes.addAll(recipes);
        parent.overhaul.fusion.allRecipes.addAll(recipes);
        addon.self.overhaul.fusion.coolantRecipes.addAll(coolantRecipes);
        parent.overhaul.fusion.allCoolantRecipes.addAll(coolantRecipes);
    }
    public Block convert(Block template){
        if(template==null)return null;
        for(Block block : allBlocks){
            if(block.name.trim().equalsIgnoreCase(template.name.trim()))return block;
        }
        for(Block block : blocks){
            if(block.name.trim().equalsIgnoreCase(template.name.trim()))return block;
        }
        throw new IllegalArgumentException("Failed to find match for block "+template.name+"!");
    }
    public BreedingBlanketRecipe convert(BreedingBlanketRecipe template){
        if(template==null)return null;
        for(BreedingBlanketRecipe breedingBlanketRecipe : allBreedingBlanketRecipes){
            if(breedingBlanketRecipe.name.trim().equalsIgnoreCase(template.name.trim()))return breedingBlanketRecipe;
        }
        for(BreedingBlanketRecipe breedingBlanketRecipe : breedingBlanketRecipes){
            if(breedingBlanketRecipe.name.trim().equalsIgnoreCase(template.name.trim()))return breedingBlanketRecipe;
        }
        throw new IllegalArgumentException("Failed to find match for breeding blanket recipe "+template.name+"!");
    }
    public Recipe convert(Recipe template){
        if(template==null)return null;
        for(Recipe recipe : allRecipes){
            if(recipe.name.trim().equalsIgnoreCase(template.name.trim()))return recipe;
        }
        for(Recipe recipe : recipes){
            if(recipe.name.trim().equalsIgnoreCase(template.name.trim()))return recipe;
        }
        throw new IllegalArgumentException("Failed to find match for recipe "+template.name+"!");
    }
    public CoolantRecipe convert(CoolantRecipe template){
        if(template==null)return null;
        for(CoolantRecipe coolantRecipe : allCoolantRecipes){
            if(coolantRecipe.name.trim().equalsIgnoreCase(template.name.trim()))return coolantRecipe;
        }
        for(CoolantRecipe coolantRecipe : coolantRecipes){
            if(coolantRecipe.name.trim().equalsIgnoreCase(template.name.trim()))return coolantRecipe;
        }
        throw new IllegalArgumentException("Failed to find match for coolant recipe "+template.name+"!");
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof FusionConfiguration){
            FusionConfiguration fc = (FusionConfiguration)obj;
            return Objects.equals(fc.blocks, blocks)
                    &&Objects.equals(fc.breedingBlanketRecipes, breedingBlanketRecipes)
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
    public void convertAddon(AddonConfiguration parent, Configuration convertTo){
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