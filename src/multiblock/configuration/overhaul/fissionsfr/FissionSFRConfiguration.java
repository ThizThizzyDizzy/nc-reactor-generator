package multiblock.configuration.overhaul.fissionsfr;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import multiblock.Multiblock;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class FissionSFRConfiguration{
    public ArrayList<Block> blocks = new ArrayList<>();
    public ArrayList<Fuel> fuels = new ArrayList<>();
    public ArrayList<Source> sources = new ArrayList<>();
    public ArrayList<IrradiatorRecipe> irradiatorRecipes = new ArrayList<>();
    public ArrayList<CoolantRecipe> coolantRecipes = new ArrayList<>();
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public int coolingEfficiencyLeniency;
    public float sparsityPenaltyMult;
    public float sparsityPenaltyThreshold;
    public String[] getBlockStringList(){
        String[] strs = new String[blocks.size()];
        for(int i = 0; i<strs.length; i++){
            strs[i] = blocks.get(i).name;
        }
        return strs;
    }
    public Config save(boolean partial){
        Config config = Config.newConfig();
        config.set("minSize", minSize);
        config.set("maxSize", maxSize);
        config.set("neutronReach", neutronReach);
        config.set("coolingEfficiencyLeniency", coolingEfficiencyLeniency);
        config.set("sparsityPenaltyMult", sparsityPenaltyMult);
        config.set("sparsityPenaltyThreshold", sparsityPenaltyThreshold);
        ConfigList blocks = new ConfigList();
        for(Block block : this.blocks){
            blocks.add(block.save(this, partial));
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
        ConfigList coolantRecipes = new ConfigList();
        for(CoolantRecipe recipe : this.coolantRecipes){
            coolantRecipes.add(recipe.save());
        }
        config.set("coolantRecipes", coolantRecipes);
        return config;
    }
    public void applyPartial(FissionSFRConfiguration partial, ArrayList<Multiblock> multiblocks){
        Set<Block> usedBlocks = new HashSet<>();
        Set<Fuel> usedFuels = new HashSet<>();
        Set<Source> usedSources = new HashSet<>();
        Set<IrradiatorRecipe> usedIrradiatorRecipes = new HashSet<>();
        Set<CoolantRecipe> usedCoolantRecipes = new HashSet<>();
        for(Multiblock mb : multiblocks){
            if(mb instanceof OverhaulSFR){
                for(multiblock.overhaul.fissionsfr.Block b : ((OverhaulSFR)mb).getBlocks()){
                    usedBlocks.add(b.template);
                    if(b.fuel!=null)usedFuels.add(b.fuel);
                    if(b.source!=null)usedSources.add(b.source);
                    if(b.irradiatorRecipe!=null)usedIrradiatorRecipes.add(b.irradiatorRecipe);
                }
                usedCoolantRecipes.add(((OverhaulSFR) mb).coolantRecipe);
            }
        }
        partial.blocks.addAll(usedBlocks);
        partial.fuels.addAll(usedFuels);
        partial.sources.addAll(usedSources);
        partial.irradiatorRecipes.addAll(usedIrradiatorRecipes);
        partial.coolantRecipes.addAll(usedCoolantRecipes);
    }
    public Block convert(Block template){
        if(template==null)return null;
        for(Block block : blocks){
            if(block.name.trim().equalsIgnoreCase(template.name.trim()))return block;
        }
        throw new IllegalArgumentException("Failed to find match for block "+template.toString()+"!");
    }
    public Fuel convert(Fuel template){
        if(template==null)return null;
        for(Fuel fuel : fuels){
            if(fuel.name.trim().equalsIgnoreCase(template.name.trim()))return fuel;
        }
        throw new IllegalArgumentException("Failed to find match for fuel "+template.toString()+"!");
    }
    public Source convert(Source template){
        if(template==null)return null;
        for(Source source : sources){
            if(source.name.trim().equalsIgnoreCase(template.name.trim()))return source;
        }
        throw new IllegalArgumentException("Failed to find match for source "+template.toString()+"!");
    }
    public IrradiatorRecipe convert(IrradiatorRecipe template){
        if(template==null)return null;
        for(IrradiatorRecipe recipe : irradiatorRecipes){
            if(recipe.name.trim().equalsIgnoreCase(template.name.trim()))return recipe;
        }
        throw new IllegalArgumentException("Failed to find match for irradiator recipe "+template.toString()+"!");
    }
    public CoolantRecipe convert(CoolantRecipe template){
        if(template==null)return null;
        for(CoolantRecipe recipe : coolantRecipes){
            if(recipe.name.trim().equalsIgnoreCase(template.name.trim()))return recipe;
        }
        throw new IllegalArgumentException("Failed to find match for coolant recipe "+template.toString()+"!");
    }
    public Block convertToSFR(multiblock.configuration.overhaul.fissionmsr.Block template){
        if(template==null)return null;
        for(Block block : blocks){
            if(block.name.trim().equalsIgnoreCase(template.name.trim().toLowerCase().replace("vessel", "cell").replace("coolant heater", "heat sink").replace("standard", "water")))return block;
        }
        throw new IllegalArgumentException("Failed to find match for block "+template.toString()+"!");
    }
    public Fuel convertToSFR(multiblock.configuration.overhaul.fissionmsr.Fuel template){
        if(template==null)return null;
        for(Fuel fuel : fuels){
            if(fuel.name.trim().toLowerCase().startsWith(template.name.trim().toLowerCase().replace(" fluoride", "").replace("mf4", "mox")))return fuel;
        }
        throw new IllegalArgumentException("Failed to find match for fuel "+template.toString()+"!");
    }
    public Source convertToSFR(multiblock.configuration.overhaul.fissionmsr.Source template){
        if(template==null)return null;
        for(Source source : sources){
            if(source.name.trim().equalsIgnoreCase(template.name.trim()))return source;
        }
        throw new IllegalArgumentException("Failed to find match for source "+template.toString()+"!");
    }
}