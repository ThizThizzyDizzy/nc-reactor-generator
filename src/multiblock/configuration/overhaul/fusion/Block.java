package multiblock.configuration.overhaul.fusion;
import java.util.ArrayList;
import java.util.Objects;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.IBlockTemplate;
import multiblock.configuration.RuleContainer;
import multiblock.configuration.TextureManager;
import planner.Core;
import planner.menu.component.Searchable;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
import simplelibrary.image.Image;
public class Block extends RuleContainer<PlacementRule.BlockType, Block> implements Searchable, IBlockTemplate {
    public String name;
    public String displayName;
    public ArrayList<String> legacyNames = new ArrayList<>();
    public boolean cluster = false;
    public boolean createCluster = false;
    public boolean conductor = false;
    public boolean connector = false;
    public boolean core = false;
    public boolean electromagnet = false;
    public boolean heatingBlanket = false;
    public boolean functional;
    public boolean breedingBlanket = false;
    public boolean breedingBlanketHasBaseStats;
    public boolean breedingBlanketAugmented;
    public float breedingBlanketEfficiency;
    public float breedingBlanketHeat;
    public boolean shielding = false;
    public boolean shieldingHasBaseStats;
    public float shieldingShieldiness;
    public boolean reflector = false;
    public boolean reflectorHasBaseStats;
    public float reflectorEfficiency;
    public boolean heatsink = false;
    public boolean heatsinkHasBaseStats = false;
    public int heatsinkCooling;
    public Image texture;
    public Image displayTexture;
    public ArrayList<BlockRecipe> allRecipes = new ArrayList<>();
    /**
     * @deprecated You should probably be using allRecipes
     */
    @Deprecated
    public ArrayList<BlockRecipe> recipes = new ArrayList<>();
    public Block(String name){
        this.name = name;
    }
    public Config save(Configuration parent, FusionConfiguration configuration, boolean partial){
        Config config = Config.newConfig();
        config.set("name", name);
        if(!partial){
            if(displayName!=null)config.set("displayName", displayName);
            if(!legacyNames.isEmpty()){
                ConfigList lst = new ConfigList();
                for(String s : legacyNames)lst.add(s);
                config.set("legacyNames", lst);
            }
        }
        if(cluster)config.set("cluster", cluster);
        if(createCluster)config.set("createCluster", createCluster);
        if(conductor)config.set("conductor", conductor);
        if(connector)config.set("connector", connector);
        if(core)config.set("core", core);
        if(electromagnet)config.set("electromagnet", electromagnet);
        if(heatingBlanket)config.set("heatingBlanket", heatingBlanket);
        if(functional)config.set("functional", functional);
        if(breedingBlanket){
            Config breedingBlanketCfg = Config.newConfig();
            if(breedingBlanketHasBaseStats){
                if(!recipes.isEmpty())breedingBlanketCfg.set("hasBaseStats", true);
                if(breedingBlanketAugmented)breedingBlanketCfg.set("augmented", breedingBlanketAugmented);
                breedingBlanketCfg.set("efficiency", breedingBlanketEfficiency);
                breedingBlanketCfg.set("heat", breedingBlanketHeat);
            }
            config.set("breedingBlanket", breedingBlanketCfg);
        }
        if(shielding){
            Config shieldingCfg = Config.newConfig();
            if(shieldingHasBaseStats){
                if(!recipes.isEmpty())shieldingCfg.set("hasBaseStats", true);
                shieldingCfg.set("shieldiness", shieldingShieldiness);
            }
            config.set("shielding", shieldingCfg);
        }
        if(reflector){
            Config reflectorCfg = Config.newConfig();
            if(reflectorHasBaseStats){
                if(!recipes.isEmpty())reflectorCfg.set("hasBaseStats", true);
                reflectorCfg.set("efficiency", reflectorEfficiency);
            }
            config.set("reflector", reflectorCfg);
        }
        if(heatsink){
            Config heatsinkCfg = Config.newConfig();
            if(heatsinkHasBaseStats){
                if(!recipes.isEmpty())heatsinkCfg.set("hasBaseStats", true);
                heatsinkCfg.set("cooling", heatsinkCooling);
            }
            config.set("heatsink", heatsinkCfg);
        }
        if(!partial&&texture!=null){
            ConfigNumberList tex = new ConfigNumberList();
            tex.add(texture.getWidth());
            for(int x = 0; x<texture.getWidth(); x++){
                for(int y = 0; y<texture.getHeight(); y++){
                    tex.add(texture.getRGB(x, y));
                }
            }
            config.set("texture", tex);
        }
        if(!rules.isEmpty()){
            ConfigList ruls = new ConfigList();
            for(AbstractPlacementRule<PlacementRule.BlockType, Block> rule : rules){
                ruls.add(rule.save(parent, configuration));
            }
            config.set("rules", ruls);
        }
        if(!recipes.isEmpty()){
            ConfigList recipesCfg = new ConfigList();
            for(BlockRecipe recipe : recipes){
                recipesCfg.add(recipe.save(this, partial));
            }
            config.set("recipes", recipesCfg);
        }
        return config;
    }
    public void setTexture(Image image){
        texture = image;
        displayTexture = TextureManager.convert(image);
    }
    @Override
    public String toString(){
        return name;
    }
    @Override
    public boolean stillEquals(RuleContainer rc){
        Block b = (Block)rc;
        return Objects.equals(b.name, name)
                &&Objects.equals(b.displayName, displayName)
                &&b.legacyNames.equals(legacyNames)
                &&b.cluster==cluster
                &&b.createCluster==createCluster
                &&b.conductor==conductor
                &&b.connector==connector
                &&b.core==core
                &&b.electromagnet==electromagnet
                &&b.heatingBlanket==heatingBlanket
                &&b.functional==functional
                &&b.breedingBlanket==breedingBlanket
                &&b.breedingBlanketHasBaseStats==breedingBlanketHasBaseStats
                &&b.breedingBlanketAugmented==breedingBlanketAugmented
                &&b.breedingBlanketEfficiency==breedingBlanketEfficiency
                &&b.breedingBlanketHeat==breedingBlanketHeat
                &&b.shielding==shielding
                &&b.shieldingHasBaseStats==shieldingHasBaseStats
                &&b.shieldingShieldiness==shieldingShieldiness
                &&b.reflector==reflector
                &&b.reflectorHasBaseStats==reflectorHasBaseStats
                &&b.reflectorEfficiency==reflectorEfficiency
                &&b.heatsink==heatsink
                &&b.heatsinkHasBaseStats==heatsinkHasBaseStats
                &&b.heatsinkCooling==heatsinkCooling
                &&Core.areImagesEqual(b.texture, texture)
                &&b.recipes.equals(recipes);
    }
    public BlockRecipe convert(BlockRecipe template){
        if(template==null)return null;
        for(BlockRecipe recipe : allRecipes){
            if(recipe.inputName.trim().equalsIgnoreCase(template.inputName.trim()))return recipe;
        }
        for(BlockRecipe recipe : recipes){
            if(recipe.inputName.trim().equalsIgnoreCase(template.inputName.trim()))return recipe;
        }
        throw new IllegalArgumentException("Failed to find match for block recipe "+template.inputName+"!");
    }
    @Override
    public ArrayList<String> getLegacyNames(){
        ArrayList<String> allNames = new ArrayList<>(legacyNames);
        allNames.add(name);
        return allNames;
    }
    @Override
    public String getDisplayName(){
        return displayName==null?name:displayName;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> nams = getLegacyNames();
        nams.add(getDisplayName());
        for(AbstractPlacementRule<PlacementRule.BlockType, Block> r : rules)nams.addAll(r.getSearchableNames());
        return nams;
    }
    public Image getDisplayTexture() {
        return displayTexture;
    }
}