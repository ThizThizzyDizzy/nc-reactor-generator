package multiblock.configuration.overhaul.fissionmsr;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import multiblock.Multiblock;
import multiblock.configuration.AddonConfiguration;
import multiblock.configuration.Configuration;
import multiblock.configuration.PartialConfiguration;
import multiblock.configuration.overhaul.OverhaulConfiguration;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class FissionMSRConfiguration{
    public ArrayList<Block> allBlocks = new ArrayList<>();
    public ArrayList<Fuel> allFuels = new ArrayList<>();
    public ArrayList<Source> allSources = new ArrayList<>();
    public ArrayList<IrradiatorRecipe> allIrradiatorRecipes = new ArrayList<>();
    /**
     * @deprecated You should probably be using allBlocks
     */
    @Deprecated
    public ArrayList<Block> blocks = new ArrayList<>();
    /**
     * @deprecated You should probably be using allFuells
     */
    @Deprecated
    public ArrayList<Fuel> fuels = new ArrayList<>();
    /**
     * @deprecated You should probably be using allSources
     */
    @Deprecated
    public ArrayList<Source> sources = new ArrayList<>();
    /**
     * @deprecated You should probably be using allIrradiatorRecipes
     */
    @Deprecated
    public ArrayList<IrradiatorRecipe> irradiatorRecipes = new ArrayList<>();
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public int coolingEfficiencyLeniency;
    public float sparsityPenaltyMult;
    public float sparsityPenaltyThreshold;
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
            config.set("minSize", minSize);
            config.set("maxSize", maxSize);
            config.set("neutronReach", neutronReach);
            config.set("coolingEfficiencyLeniency", coolingEfficiencyLeniency);
            config.set("sparsityPenaltyMult", sparsityPenaltyMult);
            config.set("sparsityPenaltyThreshold", sparsityPenaltyThreshold);
        }
        ConfigList blocks = new ConfigList();
        for(Block block : this.blocks){
            blocks.add(block.save(parent, this, partial));
        }
        config.set("blocks", blocks);
        ConfigList fuels = new ConfigList();
        for(Fuel fuel : this.fuels){
            fuels.add(fuel.save());
        }
        config.set("fuels", fuels);
        ConfigList sources = new ConfigList();
        for(Source source : this.sources){
            sources.add(source.save());
        }
        config.set("sources", sources);
        ConfigList irradiatorRecipes = new ConfigList();
        for(IrradiatorRecipe recipe : this.irradiatorRecipes){
            irradiatorRecipes.add(recipe.save());
        }
        config.set("irradiatorRecipes", irradiatorRecipes);
        return config;
    }
    public void apply(FissionMSRConfiguration partial, ArrayList<Multiblock> multiblocks, PartialConfiguration parent){
        Set<Block> usedBlocks = new HashSet<>();
        Set<Fuel> usedFuels = new HashSet<>();
        Set<Source> usedSources = new HashSet<>();
        Set<IrradiatorRecipe> usedIrradiatorRecipes = new HashSet<>();
        for(Multiblock mb : multiblocks){
            if(mb instanceof OverhaulMSR){
                for(multiblock.overhaul.fissionmsr.Block b : ((OverhaulMSR)mb).getBlocks()){
                    usedBlocks.add(b.template);
                    if(b.fuel!=null)usedFuels.add(b.fuel);
                    if(b.source!=null)usedSources.add(b.source);
                    if(b.irradiatorRecipe!=null)usedIrradiatorRecipes.add(b.irradiatorRecipe);
                }
            }
        }
        partial.blocks.addAll(usedBlocks);
        partial.fuels.addAll(usedFuels);
        partial.sources.addAll(usedSources);
        partial.irradiatorRecipes.addAll(usedIrradiatorRecipes);
        parent.overhaul.fissionMSR.allBlocks.addAll(usedBlocks);
        parent.overhaul.fissionMSR.allFuels.addAll(usedFuels);
        parent.overhaul.fissionMSR.allSources.addAll(usedSources);
        parent.overhaul.fissionMSR.allIrradiatorRecipes.addAll(usedIrradiatorRecipes);
    }
    public void apply(AddonConfiguration addon, Configuration parent){
        Set<Block> usedBlocks = new HashSet<>();
        for(Block b : blocks){
            usedBlocks.addAll(getAllUsedBlocks(b));
            usedBlocks.removeAll(blocks);
        }
        //parent blocks
        ArrayList<Block> theBlocks = new ArrayList<>();
        for(Block b : parent.overhaul.fissionMSR.blocks){
            if(usedBlocks.contains(b)){
                theBlocks.add(b);
            }
        }
        addon.overhaul.fissionMSR.allBlocks.addAll(theBlocks);
        addon.overhaul.fissionMSR.blocks.addAll(theBlocks);
        //self blocks
        addon.self.overhaul.fissionMSR.blocks.addAll(blocks);
        addon.overhaul.fissionMSR.allBlocks.addAll(blocks);
        //addon blocks
        for(Configuration addn : parent.addons){
            theBlocks = new ArrayList<>();
            if(addn.overhaul!=null&&addn.overhaul.fissionMSR!=null){
                for(Block b : addn.overhaul.fissionMSR.blocks){
                    if(usedBlocks.contains(b)){
                        theBlocks.add(b);
                    }
                }
            }
            addon.overhaul.fissionMSR.allBlocks.addAll(theBlocks);
            if(!theBlocks.isEmpty()){
                boolean foundMatch = false;
                for(Configuration c : addon.addons){
                    if(c.overhaulNameMatches(addn)){
                        foundMatch = true;
                        c.overhaul.fissionMSR.blocks.addAll(theBlocks);
                    }
                }
                if(!foundMatch){
                    Configuration c = new PartialConfiguration(addn.name, addn.overhaulVersion, addn.overhaulVersion);
                    addon.addons.add(c);
                    c.addon = true;
                    c.overhaul = new OverhaulConfiguration();
                    c.overhaul.fissionMSR = new FissionMSRConfiguration();
                    c.overhaul.fissionMSR.blocks.addAll(theBlocks);
                }
            }
        }
        addon.self.overhaul.fissionMSR.fuels.addAll(fuels);
        parent.overhaul.fissionMSR.allFuels.addAll(fuels);
        addon.self.overhaul.fissionMSR.sources.addAll(sources);
        parent.overhaul.fissionMSR.allSources.addAll(sources);
        addon.self.overhaul.fissionMSR.irradiatorRecipes.addAll(irradiatorRecipes);
        parent.overhaul.fissionMSR.allIrradiatorRecipes.addAll(irradiatorRecipes);
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
    public Fuel convert(Fuel template){
        if(template==null)return null;
        for(Fuel fuel : allFuels){
            if(fuel.name.trim().equalsIgnoreCase(template.name.trim()))return fuel;
        }
        for(Fuel fuel : fuels){
            if(fuel.name.trim().equalsIgnoreCase(template.name.trim()))return fuel;
        }
        throw new IllegalArgumentException("Failed to find match for fuel "+template.name+"!");
    }
    public Source convert(Source template){
        if(template==null)return null;
        for(Source source : allSources){
            if(source.name.trim().equalsIgnoreCase(template.name.trim()))return source;
        }
        for(Source source : sources){
            if(source.name.trim().equalsIgnoreCase(template.name.trim()))return source;
        }
        throw new IllegalArgumentException("Failed to find match for source "+template.name+"!");
    }
    public IrradiatorRecipe convert(IrradiatorRecipe template){
        if(template==null)return null;
        for(IrradiatorRecipe recipe : allIrradiatorRecipes){
            if(recipe.name.trim().equalsIgnoreCase(template.name.trim()))return recipe;
        }
        for(IrradiatorRecipe recipe : irradiatorRecipes){
            if(recipe.name.trim().equalsIgnoreCase(template.name.trim()))return recipe;
        }
        throw new IllegalArgumentException("Failed to find match for irradiator recipe "+template.name+"!");
    }
    public Block convertToMSR(multiblock.configuration.overhaul.fissionsfr.Block template){
        if(template==null)return null;
        for(Block block : allBlocks){
            if(block.name.trim().equalsIgnoreCase(template.name.trim().toLowerCase(Locale.ENGLISH).replace("cell", "vessel").replace("water heat", "standard heat").replace("heat sink", "coolant heater")))return block;
        }
        for(Block block : blocks){
            if(block.name.trim().equalsIgnoreCase(template.name.trim().toLowerCase(Locale.ENGLISH).replace("cell", "vessel").replace("water heat", "standard heat").replace("heat sink", "coolant heater")))return block;
        }
        throw new IllegalArgumentException("Failed to find match for block "+template.name+"!");
    }
    public Fuel convertToMSR(multiblock.configuration.overhaul.fissionsfr.Fuel template){
        if(template==null)return null;
        for(Fuel fuel : allFuels){
            if(fuel.name.trim().toLowerCase(Locale.ENGLISH).startsWith(template.name.trim().toLowerCase(Locale.ENGLISH).replace(" oxide", "").replace(" nitride", "").replace("-zirconium alloy", "").replace("mox", "mf4").replace("mni", "mf4").replace("mza", "mf4")))return fuel;
        }
        for(Fuel fuel : fuels){
            if(fuel.name.trim().toLowerCase(Locale.ENGLISH).startsWith(template.name.trim().toLowerCase(Locale.ENGLISH).replace(" oxide", "").replace(" nitride", "").replace("-zirconium alloy", "").replace("mox", "mf4").replace("mni", "mf4").replace("mza", "mf4")))return fuel;
        }
        throw new IllegalArgumentException("Failed to find match for fuel "+template.name+"!");
    }
    public Source convertToMSR(multiblock.configuration.overhaul.fissionsfr.Source template){
        if(template==null)return null;
        for(Source source : allSources){
            if(source.name.trim().equalsIgnoreCase(template.name.trim()))return source;
        }
        for(Source source : sources){
            if(source.name.trim().equalsIgnoreCase(template.name.trim()))return source;
        }
        throw new IllegalArgumentException("Failed to find match for source "+template.name+"!");
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof FissionMSRConfiguration){
            FissionMSRConfiguration fsfrc = (FissionMSRConfiguration)obj;
            return Objects.equals(fsfrc.blocks, blocks)
                    &&Objects.equals(fsfrc.fuels, fuels)
                    &&Objects.equals(fsfrc.sources, sources)
                    &&Objects.equals(fsfrc.irradiatorRecipes, irradiatorRecipes)
                    &&minSize==fsfrc.minSize
                    &&maxSize==fsfrc.maxSize
                    &&neutronReach==fsfrc.neutronReach
                    &&coolingEfficiencyLeniency==fsfrc.coolingEfficiencyLeniency
                    &&sparsityPenaltyMult==fsfrc.sparsityPenaltyMult
                    &&sparsityPenaltyThreshold==fsfrc.sparsityPenaltyThreshold;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}