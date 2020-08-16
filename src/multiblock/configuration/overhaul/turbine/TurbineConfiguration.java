package multiblock.configuration.overhaul.turbine;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import multiblock.Multiblock;
import multiblock.overhaul.turbine.OverhaulTurbine;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class TurbineConfiguration{
    public ArrayList<Blade> blades = new ArrayList<>();
    public ArrayList<Coil> coils = new ArrayList<>();
    public ArrayList<Recipe> recipes = new ArrayList<>();
    public int minWidth;
    public int minLength;
    public int maxSize;
    public int fluidPerBlade;
    public float throughputEfficiencyLeniency;
    public float throughputFactor;
    public float powerBonus;
    public String[] getCoilStringList(){
        String[] strs = new String[coils.size()];
        for(int i = 0; i<strs.length; i++){
            strs[i] = coils.get(i).name;
        }
        return strs;
    }
    public Config save(boolean partial){
       Config config = Config.newConfig();
       config.set("minWidth", minWidth);
       config.set("minLength", minLength);
       config.set("maxSize", maxSize);
       config.set("fluidPerBlade", fluidPerBlade);
       config.set("throughputEfficiencyLeniency", throughputEfficiencyLeniency);
       config.set("throughputFactor", throughputFactor);
       config.set("powerBonus", powerBonus);
        ConfigList blades = new ConfigList();
        for(Blade blade : this.blades){
            blades.add(blade.save(this, partial));
        }
        config.set("blades", blades);
        ConfigList coils = new ConfigList();
        for(Coil coil : this.coils){
            coils.add(coil.save(this, partial));
        }
        config.set("coils", coils);
        ConfigList recipes = new ConfigList();
        for(Recipe recipe : this.recipes){
            recipes.add(recipe.save());
        }
        config.set("recipes", recipes);
        return config;
    }
    public void applyPartial(TurbineConfiguration partial, ArrayList<Multiblock> multiblocks){
        Set<Blade> usedBlades = new HashSet<>();
        Set<Coil> usedCoils = new HashSet<>();
        Set<Recipe> usedRecipes = new HashSet<>();
        for(Multiblock mb : multiblocks){
            if(mb instanceof OverhaulTurbine){
                for(multiblock.overhaul.turbine.Block b : ((OverhaulTurbine)mb).getBlocks()){
                    if(b.coil!=null)usedCoils.add(b.coil);
                    if(b.blade!=null)usedBlades.add(b.blade);
                }
                usedRecipes.add(((OverhaulTurbine)mb).recipe);
            }
        }
        partial.blades.addAll(usedBlades);
        partial.coils.addAll(usedCoils);
        partial.recipes.addAll(usedRecipes);
    }
    public Blade convert(Blade template){
        if(template==null)return null;
        for(Blade blade : blades){
            if(blade.name.trim().equalsIgnoreCase(template.name.trim()))return blade;
        }
        throw new IllegalArgumentException("Failed to find match for blade "+template.toString()+"!");
    }
    public Coil convert(Coil template){
        if(template==null)return null;
        for(Coil coil : coils){
            if(coil.name.trim().equalsIgnoreCase(template.name.trim()))return coil;
        }
        throw new IllegalArgumentException("Failed to find match for coil "+template.toString()+"!");
    }
    public Recipe convert(Recipe template){
        if(template==null)return null;
        for(Recipe recipe : recipes){
            if(recipe.name.trim().equalsIgnoreCase(template.name.trim()))return recipe;
        }
        throw new IllegalArgumentException("Failed to find match for recipe "+template.toString()+"!");
    }
}