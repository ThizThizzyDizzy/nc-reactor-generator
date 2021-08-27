package multiblock.configuration.overhaul.fissionsfr;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.IBlockTemplate;
import multiblock.configuration.RuleContainer;
import multiblock.configuration.TextureManager;
import planner.Core;
import planner.Pinnable;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
import simplelibrary.image.Image;
public class Block extends RuleContainer<PlacementRule.BlockType, Block> implements Pinnable, IBlockTemplate {
    public static Block controller(String name, String displayName, String texture){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.setTexture(TextureManager.getImage(texture));
        block.controller = true;
        block.casing = true;
        return block;
    }
    public static Block casing(String name, String displayName, String texture, boolean edge){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.setTexture(TextureManager.getImage(texture));
        block.casing = true;
        block.casingEdge = edge;
        return block;
    }
    public static Block vent(String name, String displayName, String texture, String outputDisplayName, String outputTexture){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.setTexture(TextureManager.getImage(texture));
        block.casing = true;
        block.coolantVent = true;
        block.coolantVentOutputDisplayName = outputDisplayName;
        block.setCoolantVentOutputTexture(TextureManager.getImage(outputTexture));
        return block;
    }
    public static Block port(Block parent, String name, String displayName, String texture, String outputDisplayName, String outputTexture){
        Block block = new Block(name);
        block.parent = parent;
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.setTexture(TextureManager.getImage(texture));
        block.portOutputDisplayName = outputDisplayName;
        block.setPortOutputTexture(TextureManager.getImage(outputTexture));
        return block;
    }
    public static Block source(String name, String displayName, String texture, float efficiency){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName.replace(" Neutron Source", ""));
        block.setTexture(TextureManager.getImage(texture));
        block.casing = true;
        block.source = true;
        block.sourceEfficiency = efficiency;
        return block;
    }
    public static Block heatsink(String name, String displayName, int cooling, String texture, PlacementRule... rules){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.heatsink = true;
        block.heatsinkHasBaseStats = true;
        block.heatsinkCooling = cooling;
        for(PlacementRule r : rules){
            block.rules.add(r);
        }
        block.setTexture(TextureManager.getImage(texture));
        block.functional = true;
        block.cluster = true;
        return block;
    }
    public static Block cell(String name, String displayName, String texture){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.fuelCell = true;
        block.cluster = true;
        block.createCluster = true;
        block.blocksLOS = true;
        block.functional = true;
        block.setTexture(TextureManager.getImage(texture));
        return block;
    }
    public static Block irradiator(String name, String displayName, String texture){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.cluster = true;
        block.createCluster = true;
        block.irradiator = true;
        block.functional = true;
        block.blocksLOS = true;
        block.setTexture(TextureManager.getImage(texture));
        return block;
    }
    public static Block conductor(String name, String displayName, String texture){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.cluster = true;//because conductors connect clusters together
        block.setTexture(TextureManager.getImage(texture));
        return block;
    }
    public static Block moderator(String name, String displayName, String texture, int flux, float efficiency){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.moderator = true;
        block.moderatorHasBaseStats = true;
        block.moderatorActive = true;
        block.moderatorFlux = flux;
        block.moderatorEfficiency = efficiency;
        block.setTexture(TextureManager.getImage(texture));
        block.functional = true;
        return block;
    }
    public static Block reflector(String name, String displayName, String texture, float efficiency, float reflectivity){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.reflector = true;
        block.reflectorHasBaseStats = true;
        block.reflectorEfficiency = efficiency;
        block.reflectorReflectivity = reflectivity;
        block.functional = true;
        block.blocksLOS = true;
        block.setTexture(TextureManager.getImage(texture));
        return block;
    }
    public static Block shield(String name, String displayName, String texture, String closedTexture, int heatPerFlux, float efficiency){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.shield = true;
        block.moderator = true;
        block.functional = true;
        block.cluster = true;
        block.createCluster = true;
        block.shieldHasBaseStats = true;
        block.shieldHeat = heatPerFlux;
        block.shieldEfficiency = efficiency;
        block.moderatorHasBaseStats = true;
        block.moderatorEfficiency = efficiency;
        block.setTexture(TextureManager.getImage(texture));
        block.setShieldClosedTexture(TextureManager.getImage(closedTexture));
        return block;
    }
    public String name;
    public String displayName;
    public ArrayList<String> legacyNames = new ArrayList<>();
    public boolean cluster = false;
    public boolean createCluster = false;
    public boolean conductor = false;
    public boolean functional = false;
    public boolean blocksLOS = false;
    public boolean casing = false;
    public boolean casingEdge = false;
    public boolean coolantVent = false;
    public String coolantVentOutputDisplayName;
    public Image coolantVentOutputTexture;
    public Image coolantVentOutputDisplayTexture;
    public boolean controller = false;
    public boolean fuelCell = false;
    public boolean fuelCellHasBaseStats;
    public float fuelCellEfficiency;
    public int fuelCellHeat;
    public int fuelCellCriticality;
    public boolean fuelCellSelfPriming;
    public boolean irradiator = false;
    public boolean irradiatorHasBaseStats;
    public float irradiatorEfficiency;
    public float irradiatorHeat;
    public boolean reflector = false;
    public boolean reflectorHasBaseStats;
    public float reflectorEfficiency;
    public float reflectorReflectivity;
    public boolean moderator = false;
    public boolean moderatorHasBaseStats;
    public int moderatorFlux;
    public float moderatorEfficiency;
    public boolean moderatorActive;
    public boolean shield = false;
    public boolean shieldHasBaseStats;
    public int shieldHeat;
    public float shieldEfficiency;
    public Image shieldClosedTexture;
    public Image shieldClosedDisplayTexture;
    public boolean heatsink;
    public boolean heatsinkHasBaseStats;
    public int heatsinkCooling;
    public boolean source;
    public float sourceEfficiency;
    public Image texture;
    public Image displayTexture;
    public Block port;
    public String portOutputDisplayName;
    public Image portOutputTexture;
    public Image portOutputDisplayTexture;
    public Block parent;//if this is a port
    public ArrayList<BlockRecipe> allRecipes = new ArrayList<>();
    /**
     * @deprecated You should probably be using allRecipes
     */
    @Deprecated
    public ArrayList<BlockRecipe> recipes = new ArrayList<>();
    public Block(String name){
        this.name = name;
    }
    public Config save(Configuration parent, FissionSFRConfiguration configuration, boolean partial){
        Config config = Config.newConfig();
        config.set("name", name);
        boolean isHereJustToHoldRecipes = false;
        if(parent!=null){
            for(Block b : parent.overhaul.fissionSFR.blocks){
                if(b.name.equals(name))isHereJustToHoldRecipes = true;
            }
        }
        if(isHereJustToHoldRecipes){
            if(fuelCell){
                config.set("fuelCell", Config.newConfig());
            }
            if(irradiator){
                config.set("irradiator", Config.newConfig());
            }
            if(reflector){
                config.set("reflector", Config.newConfig());
            }
            if(moderator){
                config.set("moderator", Config.newConfig());
            }
            if(shield){
                config.set("shield", Config.newConfig());
            }
            if(heatsink){
                config.set("heatsink", Config.newConfig());
            }
        }else{
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
            if(functional)config.set("functional", functional);
            if(blocksLOS)config.set("blocksLOS", blocksLOS);
            if(casing)config.set("casing", casing);
            if(casingEdge)config.set("casingEdge", casingEdge);
            if(coolantVent){
                Config coolantVentCfg = Config.newConfig();
                if(!partial&&coolantVentOutputTexture!=null){
                    ConfigNumberList tex = new ConfigNumberList();
                    tex.add(coolantVentOutputTexture.getWidth());
                    for(int x = 0; x<coolantVentOutputTexture.getWidth(); x++){
                        for(int y = 0; y<coolantVentOutputTexture.getHeight(); y++){
                            tex.add(coolantVentOutputTexture.getRGB(x, y));
                        }
                    }
                    coolantVentCfg.set("outTexture", tex);
                }
                if(!partial&&coolantVentOutputDisplayName!=null)coolantVentCfg.set("outDisplayName", coolantVentOutputDisplayName);
                config.set("coolantVent", coolantVentCfg);
            }
            if(controller)config.set("controller", controller);
            if(fuelCell){
                Config fuelCellCfg = Config.newConfig();
                if(fuelCellHasBaseStats){
                    if(!recipes.isEmpty())fuelCellCfg.set("hasBaseStats", true);
                    fuelCellCfg.set("efficiency", fuelCellEfficiency);
                    fuelCellCfg.set("heat", fuelCellHeat);
                    fuelCellCfg.set("criticality", fuelCellCriticality);
                    if(fuelCellSelfPriming)fuelCellCfg.set("selfPriming", fuelCellSelfPriming);
                }
                config.set("fuelCell", fuelCellCfg);
            }
            if(irradiator){
                Config irradiatorCfg = Config.newConfig();
                if(irradiatorHasBaseStats){
                    if(!recipes.isEmpty())irradiatorCfg.set("hasBaseStats", true);
                    irradiatorCfg.set("efficiency", irradiatorEfficiency);
                    irradiatorCfg.set("heat", irradiatorHeat);
                }
                config.set("irradiator", irradiatorCfg);
            }
            if(reflector){
                Config reflectorCfg = Config.newConfig();
                if(reflectorHasBaseStats){
                    if(!recipes.isEmpty())reflectorCfg.set("hasBaseStats", true);
                    reflectorCfg.set("efficiency", reflectorEfficiency);
                    reflectorCfg.set("reflectivity", reflectorReflectivity);
                }
                config.set("reflector", reflectorCfg);
            }
            if(moderator){
                Config moderatorCfg = Config.newConfig();
                if(moderatorHasBaseStats){
                    if(!recipes.isEmpty())moderatorCfg.set("hasBaseStats", true);
                    moderatorCfg.set("flux", moderatorFlux);
                    moderatorCfg.set("efficiency", moderatorEfficiency);
                    if(moderatorActive)moderatorCfg.set("active", true);
                }
                config.set("moderator", moderatorCfg);
            }
            if(shield){
                Config shieldCfg = Config.newConfig();
                if(shieldHasBaseStats){
                    if(!recipes.isEmpty())shieldCfg.set("hasBaseStats", true);
                    shieldCfg.set("heat", shieldHeat);
                    shieldCfg.set("efficiency", shieldEfficiency);
                    if(!partial&&shieldClosedTexture!=null){
                        ConfigNumberList tex = new ConfigNumberList();
                        tex.add(shieldClosedTexture.getWidth());
                        for(int x = 0; x<shieldClosedTexture.getWidth(); x++){
                            for(int y = 0; y<shieldClosedTexture.getHeight(); y++){
                                tex.add(shieldClosedTexture.getRGB(x, y));
                            }
                        }
                        shieldCfg.set("closedTexture", tex);
                    }
                }
                config.set("shield", shieldCfg);
            }
            if(heatsink){
                Config heatsinkCfg = Config.newConfig();
                if(heatsinkHasBaseStats){
                    if(!recipes.isEmpty())heatsinkCfg.set("hasBaseStats", true);
                    heatsinkCfg.set("cooling", heatsinkCooling);
                }
                config.set("heatsink", heatsinkCfg);
            }
            if(source){
                Config sourceCfg = Config.newConfig();
                sourceCfg.set("efficiency", sourceEfficiency);
                config.set("source", sourceCfg);
            }
            if(!partial){
                if(texture!=null){
                    ConfigNumberList tex = new ConfigNumberList();
                    tex.add(texture.getWidth());
                    for(int x = 0; x<texture.getWidth(); x++){
                        for(int y = 0; y<texture.getHeight(); y++){
                            tex.add(texture.getRGB(x, y));
                        }
                    }
                    config.set("texture", tex);
                }
            }
            if(!recipes.isEmpty()){
                Config portCfg = Config.newConfig();
                portCfg.set("name", port.name);
                if(!partial){
                    if(port.displayName!=null)portCfg.set("inputDisplayName", port.displayName);
                    if(port.texture!=null){
                        ConfigNumberList tex = new ConfigNumberList();
                        tex.add(port.texture.getWidth());
                        for(int x = 0; x<port.texture.getWidth(); x++){
                            for(int y = 0; y<port.texture.getHeight(); y++){
                                tex.add(port.texture.getRGB(x, y));
                            }
                        }
                        portCfg.set("inputTexture", tex);
                    }
                    if(port.portOutputDisplayName!=null)portCfg.set("outputDisplayName", port.portOutputDisplayName);
                    if(port.portOutputTexture!=null){
                        ConfigNumberList tex = new ConfigNumberList();
                        tex.add(port.portOutputTexture.getWidth());
                        for(int x = 0; x<port.portOutputTexture.getWidth(); x++){
                            for(int y = 0; y<port.portOutputTexture.getHeight(); y++){
                                tex.add(port.portOutputTexture.getRGB(x, y));
                            }
                        }
                        portCfg.set("outputTexture", tex);
                    }
                }
                config.set("port", portCfg);
            }
            if(!rules.isEmpty()){
                ConfigList ruls = new ConfigList();
                for(AbstractPlacementRule<PlacementRule.BlockType, Block> rule : rules){
                    ruls.add(rule.save(parent, configuration));
                }
                config.set("rules", ruls);
            }
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
    public Image getTexture(){
        return texture;
    }
    public void setTexture(Image image){
        texture = image;
        displayTexture = TextureManager.convert(image);
    }
    public Image getCoolantVentOutputTexture(){
        return coolantVentOutputTexture;
    }
    public void setCoolantVentOutputTexture(Image image){
        coolantVentOutputTexture = image;
        coolantVentOutputDisplayTexture = TextureManager.convert(image);
    }
    public Image getPortOutputTexture(){
        return portOutputTexture;
    }
    public void setPortOutputTexture(Image image){
        portOutputTexture = image;
        portOutputDisplayTexture = TextureManager.convert(image);
    }
    public Image getShieldClosedTexture(){
        return shieldClosedTexture;
    }
    public void setShieldClosedTexture(Image image){
        shieldClosedTexture = image;
        shieldClosedDisplayTexture = TextureManager.convert(image);
    }
    public BlockRecipe convert(BlockRecipe template){
        if(template==null)return null;
        if(parent!=null)return parent.convert(template);
        for(BlockRecipe recipe : allRecipes){
            for(String inputName : recipe.getLegacyNames()){
                if(inputName.equals(template.inputName))return recipe;
            }
        }
        for(BlockRecipe recipe : recipes){
            for(String inputName : recipe.getLegacyNames()){
                if(inputName.equals(template.inputName))return recipe;
            }
        }
        throw new IllegalArgumentException("Failed to find match for block recipe "+template.inputName+"!");
    }
    public BlockRecipe convertToSFR(multiblock.configuration.overhaul.fissionmsr.BlockRecipe template){
        if(template==null)return null;
        if(parent!=null)return parent.convertToSFR(template);
        if(heatsink)return null;
        for(BlockRecipe recipe : allRecipes){
            for(String inputName : recipe.getLegacyNames()){
                if(inputName.equals(template.inputName)||inputName.trim().toLowerCase(Locale.ROOT).startsWith(template.getInputDisplayName().trim().toLowerCase(Locale.ROOT).replace(" fluoride", "").replace("mf4", "mox")))return recipe;
            }
        }
        for(BlockRecipe recipe : recipes){
            for(String inputName : recipe.getLegacyNames()){
                if(inputName.equals(template.inputName)||inputName.trim().toLowerCase(Locale.ROOT).startsWith(template.getInputDisplayName().trim().toLowerCase(Locale.ROOT).replace(" fluoride", "").replace("mf4", "mox")))return recipe;
            }
        }
        throw new IllegalArgumentException("Failed to find match for block recipe "+template.inputName+"!");
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
                &&b.functional==functional
                &&b.blocksLOS==blocksLOS
                &&b.casing==casing
                &&b.casingEdge==casingEdge
                &&b.coolantVent==coolantVent
                &&Objects.equals(b.coolantVentOutputDisplayName, coolantVentOutputDisplayName)
                &&Core.areImagesEqual(b.coolantVentOutputTexture, coolantVentOutputTexture)
                &&b.controller==controller
                &&b.fuelCell==fuelCell
                &&b.fuelCellHasBaseStats==fuelCellHasBaseStats
                &&b.fuelCellEfficiency==fuelCellEfficiency
                &&b.fuelCellHeat==fuelCellHeat
                &&b.fuelCellCriticality==fuelCellCriticality
                &&b.fuelCellSelfPriming==fuelCellSelfPriming
                &&b.irradiator==irradiator
                &&b.irradiatorHasBaseStats==irradiatorHasBaseStats
                &&b.irradiatorEfficiency==irradiatorEfficiency
                &&b.irradiatorHeat==irradiatorHeat
                &&b.reflector==reflector
                &&b.reflectorHasBaseStats==reflectorHasBaseStats
                &&b.reflectorEfficiency==reflectorEfficiency
                &&b.reflectorReflectivity==reflectorReflectivity
                &&b.moderator==moderator
                &&b.moderatorHasBaseStats==moderatorHasBaseStats
                &&b.moderatorFlux==moderatorFlux
                &&b.moderatorEfficiency==moderatorEfficiency
                &&b.moderatorActive==moderatorActive
                &&b.shield==shield
                &&b.shieldHasBaseStats==shieldHasBaseStats
                &&b.shieldHeat==shieldHeat
                &&b.shieldEfficiency==shieldEfficiency
                &&Core.areImagesEqual(b.shieldClosedTexture, shieldClosedTexture)
                &&b.heatsink==heatsink
                &&b.heatsinkHasBaseStats==heatsinkHasBaseStats
                &&b.heatsinkCooling==heatsinkCooling
                &&b.source==source
                &&b.sourceEfficiency==sourceEfficiency
                &&Core.areImagesEqual(b.texture, texture)
                &&Objects.equals(b.port, port)
                &&Objects.equals(b.portOutputDisplayName, portOutputDisplayName)
                &&Core.areImagesEqual(b.portOutputTexture, portOutputTexture)
                &&b.recipes.equals(recipes);
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String getDisplayName(){
        return displayName==null?name:displayName;
    }
    @Override
    public ArrayList<String> getLegacyNames(){
        ArrayList<String> allNames = new ArrayList<>(legacyNames);
        allNames.add(name);
        return allNames;
    }
    public String getCoolantVentOutputDisplayName(){
        return coolantVentOutputDisplayName==null?name:coolantVentOutputDisplayName;
    }
    public String getPortOutputDisplayName(){
        return portOutputDisplayName==null?name:portOutputDisplayName;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> nams = getLegacyNames();
        nams.add(getDisplayName());
        for(AbstractPlacementRule<PlacementRule.BlockType, Block> r : rules)nams.addAll(r.getSearchableNames());
        return nams;
    }
    @Override
    public Image getDisplayTexture() {
        return displayTexture;
    }
    @Override
    public String getPinnedName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }
    public void setCoolantVentOutputDisplayName(String coolantVentOutputDisplayName){
        this.coolantVentOutputDisplayName = coolantVentOutputDisplayName;
    }
    public void setPortOutputDisplayName(String portOutputDisplayName){
        this.portOutputDisplayName = portOutputDisplayName;
    }
    public void setLegacyNames(ArrayList<String> legacyNames){
        this.legacyNames = new ArrayList<>(legacyNames);
    }
    public boolean isCluster(){
        return cluster;
    }
    public boolean isCreateCluster(){
        return createCluster;
    }
    public boolean isConductor(){
        return conductor;
    }
    public boolean isFunctional(){
        return functional;
    }
    public boolean isBlocksLOS(){
        return blocksLOS;
    }
    public boolean isCasing(){
        return casing;
    }
    public boolean isCasingEdge(){
        return casingEdge;
    }
    public boolean isCoolantVent(){
        return coolantVent;
    }
    public boolean isController(){
        return controller;
    }
    public boolean isFuelCell(){
        return fuelCell;
    }
    public boolean isFuelCellHasBaseStats(){
        return fuelCellHasBaseStats;
    }
    public float getFuelCellEfficiency(){
        return fuelCellEfficiency;
    }
    public int getFuelCellHeat(){
        return fuelCellHeat;
    }
    public int getFuelCellCriticality(){
        return fuelCellCriticality;
    }
    public boolean isFuelCellSelfPriming(){
        return fuelCellSelfPriming;
    }
    public boolean isIrradiator(){
        return irradiator;
    }
    public boolean isIrradiatorHasBaseStats(){
        return irradiatorHasBaseStats;
    }
    public float getIrradiatorEfficiency(){
        return irradiatorEfficiency;
    }
    public float getIrradiatorHeat(){
        return irradiatorHeat;
    }
    public boolean isReflector(){
        return reflector;
    }
    public boolean isReflectorHasBaseStats(){
        return reflectorHasBaseStats;
    }
    public float getReflectorEfficiency(){
        return reflectorEfficiency;
    }
    public float getReflectorReflectivity(){
        return reflectorReflectivity;
    }
    public boolean isModerator(){
        return moderator;
    }
    public boolean isModeratorHasBaseStats(){
        return moderatorHasBaseStats;
    }
    public int getModeratorFlux(){
        return moderatorFlux;
    }
    public float getModeratorEfficiency(){
        return moderatorEfficiency;
    }
    public boolean isModeratorActive(){
        return moderatorActive;
    }
    public boolean isShield(){
        return shield;
    }
    public boolean isShieldHasBaseStats(){
        return shieldHasBaseStats;
    }
    public int getShieldHeat(){
        return shieldHeat;
    }
    public float getShieldEfficiency(){
        return shieldEfficiency;
    }
    public boolean isHeatsink(){
        return heatsink;
    }
    public boolean isHeatsinkHasBaseStats(){
        return heatsinkHasBaseStats;
    }
    public int getHeatsinkCooling(){
        return heatsinkCooling;
    }
    public boolean isSource(){
        return source;
    }
    public float getSourceEfficiency(){
        return sourceEfficiency;
    }
    public void setCluster(boolean cluster){
        this.cluster = cluster;
    }
    public void setCreateCluster(boolean createCluster){
        this.createCluster = createCluster;
    }
    public void setConductor(boolean conductor){
        this.conductor = conductor;
    }
    public void setFunctional(boolean functional){
        this.functional = functional;
    }
    public void setBlocksLOS(boolean blocksLOS){
        this.blocksLOS = blocksLOS;
    }
    public void setCasing(boolean casing){
        this.casing = casing;
    }
    public void setCasingEdge(boolean casingEdge){
        this.casingEdge = casingEdge;
    }
    public void setCoolantVent(boolean coolantVent){
        this.coolantVent = coolantVent;
    }
    public void setController(boolean controller){
        this.controller = controller;
    }
    public void setFuelCell(boolean fuelCell){
        this.fuelCell = fuelCell;
    }
    public void setFuelCellHasBaseStats(boolean fuelCellHasBaseStats){
        this.fuelCellHasBaseStats = fuelCellHasBaseStats;
    }
    public void setFuelCellEfficiency(float fuelCellEfficiency){
        this.fuelCellEfficiency = fuelCellEfficiency;
    }
    public void setFuelCellHeat(int fuelCellHeat){
        this.fuelCellHeat = fuelCellHeat;
    }
    public void setFuelCellCriticality(int fuelCellCriticality){
        this.fuelCellCriticality = fuelCellCriticality;
    }
    public void setFuelCellSelfPriming(boolean fuelCellSelfPriming){
        this.fuelCellSelfPriming = fuelCellSelfPriming;
    }
    public void setIrradiator(boolean irradiator){
        this.irradiator = irradiator;
    }
    public void setIrradiatorHasBaseStats(boolean irradiatorHasBaseStats){
        this.irradiatorHasBaseStats = irradiatorHasBaseStats;
    }
    public void setIrradiatorEfficiency(float irradiatorEfficiency){
        this.irradiatorEfficiency = irradiatorEfficiency;
    }
    public void setIrradiatorHeat(float irradiatorHeat){
        this.irradiatorHeat = irradiatorHeat;
    }
    public void setReflector(boolean reflector){
        this.reflector = reflector;
    }
    public void setReflectorHasBaseStats(boolean reflectorHasBaseStats){
        this.reflectorHasBaseStats = reflectorHasBaseStats;
    }
    public void setReflectorEfficiency(float reflectorEfficiency){
        this.reflectorEfficiency = reflectorEfficiency;
    }
    public void setReflectorReflectivity(float reflectorReflectivity){
        this.reflectorReflectivity = reflectorReflectivity;
    }
    public void setModerator(boolean moderator){
        this.moderator = moderator;
    }
    public void setModeratorHasBaseStats(boolean moderatorHasBaseStats){
        this.moderatorHasBaseStats = moderatorHasBaseStats;
    }
    public void setModeratorFlux(int moderatorFlux){
        this.moderatorFlux = moderatorFlux;
    }
    public void setModeratorEfficiency(float moderatorEfficiency){
        this.moderatorEfficiency = moderatorEfficiency;
    }
    public void setModeratorActive(boolean moderatorActive){
        this.moderatorActive = moderatorActive;
    }
    public void setShield(boolean shield){
        this.shield = shield;
    }
    public void setShieldHasBaseStats(boolean shieldHasBaseStats){
        this.shieldHasBaseStats = shieldHasBaseStats;
    }
    public void setShieldHeat(int shieldHeat){
        this.shieldHeat = shieldHeat;
    }
    public void setShieldEfficiency(float shieldEfficiency){
        this.shieldEfficiency = shieldEfficiency;
    }
    public void setHeatsink(boolean heatsink){
        this.heatsink = heatsink;
    }
    public void setHeatsinkHasBaseStats(boolean heatsinkHasBaseStats){
        this.heatsinkHasBaseStats = heatsinkHasBaseStats;
    }
    public void setHeatsinkCooling(int heatsinkCooling){
        this.heatsinkCooling = heatsinkCooling;
    }
    public void setSource(boolean source){
        this.source = source;
    }
    public void setSourceEfficiency(float sourceEfficiency){
        this.sourceEfficiency = sourceEfficiency;
    }
}