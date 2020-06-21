package planner.configuration.overhaul.fissionsfr;
import java.util.ArrayList;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class FissionSFRConfiguration{
    public ArrayList<Block> blocks = new ArrayList<>();
    public ArrayList<Fuel> fuels = new ArrayList<>();
    public ArrayList<Source> sources = new ArrayList<>();
    public ArrayList<IrradiatorRecipe> irradiatorRecipes = new ArrayList<>();
    public int coolingEfficiencyLeniency;
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public float sparsityPenaltyMult;
    public float sparsityPenaltyThreshold;
    public String[] getBlockStringList(){
        String[] strs = new String[blocks.size()];
        for(int i = 0; i<strs.length; i++){
            strs[i] = blocks.get(i).name;
        }
        return strs;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("minSize", minSize);
        config.set("maxSize", maxSize);
        config.set("neutronReach", neutronReach);
        config.set("coolingEfficiencyLeniency", coolingEfficiencyLeniency);
        config.set("sparsityPenaltyMult", sparsityPenaltyMult);
        config.set("sparsityPenaltyThreshold", sparsityPenaltyThreshold);
        ConfigList blocks = new ConfigList();
        for(Block block : this.blocks){
            blocks.add(block.save());
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
        config.set("sources", new ConfigList());
        ConfigList irradiatorRecipes = new ConfigList();
        for(IrradiatorRecipe recipe : this.irradiatorRecipes){
            irradiatorRecipes.add(recipe.save());
        }
        config.set("irradiatorRecipes", irradiatorRecipes);
        return config;
    }
}