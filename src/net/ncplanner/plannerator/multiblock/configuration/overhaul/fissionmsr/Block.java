package net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr;
import java.util.ArrayList;
import java.util.Objects;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockTemplate;
import net.ncplanner.plannerator.multiblock.configuration.RuleContainer;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.writer.NCPFWriter;
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
        block.legacyNames.add(StringUtil.superRemove(displayName, " Neutron Source"));
        block.setTexture(TextureManager.getImage(texture));
        block.casing = true;
        block.source = true;
        block.sourceEfficiency = efficiency;
        return block;
    }
    public static Block heater(String name, String displayName, String texture, PlacementRule... rules){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.heater = true;
        for(PlacementRule r : rules){
            block.rules.add(r);
        }
        block.setTexture(TextureManager.getImage(texture));
        block.functional = true;
        block.cluster = true;
        block.moderator = true;
        block.moderatorHasBaseStats = true;
        return block;
    }
    public static Block vessel(String name, String displayName, String texture){
        Block block = new Block(name);
        block.displayName = displayName;
        block.legacyNames.add(displayName);
        block.fuelVessel = true;
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
    public boolean controller = false;
    public boolean fuelVessel = false;
    public boolean fuelVesselHasBaseStats;
    public float fuelVesselEfficiency;
    public int fuelVesselHeat;
    public int fuelVesselCriticality;
    public boolean fuelVesselSelfPriming;
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
    public boolean heater;
    public boolean heaterHasBaseStats;
    public int heaterCooling;
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
    public Config save(Configuration parent, FissionMSRConfiguration configuration, boolean partial){
        Config config = Config.newConfig();
        config.set("name", name);
        boolean isHereJustToHoldRecipes = false;
        if(parent!=null){
            for(Block b : parent.overhaul.fissionMSR.blocks){
                if(b.name.equals(name))isHereJustToHoldRecipes = true;
            }
        }
        if(isHereJustToHoldRecipes){
            if(fuelVessel){
                config.set("fuelVessel", Config.newConfig());
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
            if(heater){
                config.set("heater", Config.newConfig());
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
            if(controller)config.set("controller", controller);
            if(fuelVessel){
                Config fuelVesselCfg = Config.newConfig();
                if(fuelVesselHasBaseStats){
                    if(!recipes.isEmpty())fuelVesselCfg.set("hasBaseStats", true);
                    fuelVesselCfg.set("efficiency", fuelVesselEfficiency);
                    fuelVesselCfg.set("heat", fuelVesselHeat);
                    fuelVesselCfg.set("criticality", fuelVesselCriticality);
                    if(fuelVesselSelfPriming)fuelVesselCfg.set("selfPriming", fuelVesselSelfPriming);
                }
                config.set("fuelVessel", fuelVesselCfg);
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
                    if(!partial)NCPFWriter.saveTexture(shieldCfg, "closedTexture", shieldClosedTexture);
                }
                config.set("shield", shieldCfg);
            }
            if(heater){
                Config heaterCfg = Config.newConfig();
                if(heaterHasBaseStats){
                    if(!recipes.isEmpty())heaterCfg.set("hasBaseStats", true);
                    heaterCfg.set("cooling", heaterCooling);
                }
                config.set("heater", heaterCfg);
            }
            if(source){
                Config sourceCfg = Config.newConfig();
                sourceCfg.set("efficiency", sourceEfficiency);
                config.set("source", sourceCfg);
            }
            if(!partial)NCPFWriter.saveTexture(config, texture);
            if(!recipes.isEmpty()){
                Config portCfg = Config.newConfig();
                portCfg.set("name", port.name);
                if(!partial){
                    if(port.displayName!=null)portCfg.set("inputDisplayName", port.displayName);
                    NCPFWriter.saveTexture(portCfg, "inputTexture", port.texture);
                    if(port.portOutputDisplayName!=null)portCfg.set("outputDisplayName", port.portOutputDisplayName);
                    NCPFWriter.saveTexture(portCfg, "outputTexture", port.portOutputTexture);
                }
                config.set("port", portCfg);
            }
            if(!rules.isEmpty()){
                ConfigList ruls = new ConfigList();
                for (AbstractPlacementRule<PlacementRule.BlockType, Block> rule : rules){
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
    public void setInternalTexture(Image other){
        int left = Math.max(0,texture.getWidth()*5/16-1);
        int right = Math.min(texture.getWidth()*11/16, texture.getWidth()-1);
        int up = Math.max(0,texture.getHeight()*5/16-1);
        int down = Math.min(texture.getHeight()*11/16, texture.getHeight()-1);
        Image displayImg = new Image(texture.getWidth(), texture.getHeight());
        for(int x = 0; x<texture.getWidth(); x++){
            for(int y = 0; y<texture.getHeight(); y++){
                if(x>left&&y>up&&x<right&&y<down){
                    displayImg.setColor(x, y, TextureManager.convert(new Color(other.getRGB(x, y))));
                }else{
                    displayImg.setColor(x, y, TextureManager.convert(new Color(texture.getRGB(x, y))));
                }
            }
        }
        displayTexture = displayImg;
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
    public void setInternalShieldClosedTexture(Image other){
        int left = Math.max(0,shieldClosedTexture.getWidth()*5/16-1);
        int right = Math.min(shieldClosedTexture.getWidth()*11/16, shieldClosedTexture.getWidth()-1);
        int up = Math.max(0,shieldClosedTexture.getHeight()*5/16-1);
        int down = Math.min(shieldClosedTexture.getHeight()*11/16, shieldClosedTexture.getHeight()-1);
        Image displayImg = new Image(shieldClosedTexture.getWidth(), shieldClosedTexture.getHeight());
        for(int x = 0; x<shieldClosedTexture.getWidth(); x++){
            for(int y = 0; y<shieldClosedTexture.getHeight(); y++){
                if(x>left&&y>up&&x<right&&y<down){
                    Color col = new Color(other.getRGB(x, y));
                    displayImg.setColor(x, y, TextureManager.convert(col));
                }else{
                    displayImg.setRGB(x, y, shieldClosedTexture.getRGB(x, y));
                }
            }
        }
        shieldClosedDisplayTexture = displayImg;
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
    public BlockRecipe convertToMSR(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe template){
        if(template==null)return null;
        if(parent!=null)return parent.convertToMSR(template);
        for(BlockRecipe recipe : allRecipes){
            for(String inputName : recipe.getLegacyNames()){
                if(inputName.equals(template.inputName)||StringUtil.toLowerCase(inputName).startsWith(StringUtil.superReplace(StringUtil.superRemove(StringUtil.toLowerCase(template.getInputDisplayName().trim()), " oxide", " nitride", "-zirconium alloy", ""), "mox", "mf4", "mni", "mf4", "mza", "mf4")))return recipe;
            }
        }
        for(BlockRecipe recipe : recipes){
            for(String inputName : recipe.getLegacyNames()){
                if(inputName.equals(template.inputName)||StringUtil.toLowerCase(inputName).startsWith(StringUtil.superReplace(StringUtil.superRemove(StringUtil.toLowerCase(template.getInputDisplayName().trim()), " oxide", " nitride", "-zirconium alloy", ""), "mox", "mf4", "mni", "mf4", "mza", "mf4")))return recipe;
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
                &&b.controller==controller
                &&b.fuelVessel==fuelVessel
                &&b.fuelVesselHasBaseStats==fuelVesselHasBaseStats
                &&b.fuelVesselEfficiency==fuelVesselEfficiency
                &&b.fuelVesselHeat==fuelVesselHeat
                &&b.fuelVesselCriticality==fuelVesselCriticality
                &&b.fuelVesselSelfPriming==fuelVesselSelfPriming
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
                &&b.heater==heater
                &&b.heaterHasBaseStats==heaterHasBaseStats
                &&b.heaterCooling==heaterCooling
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
    public String getPortOutputDisplayName(){
        return portOutputDisplayName==null?name:portOutputDisplayName;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        ArrayList<String> nams = getLegacyNames();
        nams.add(getDisplayName());
        return nams;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> nams = getSimpleSearchableNames();
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
    public boolean isController(){
        return controller;
    }
    public boolean isFuelVessel(){
        return fuelVessel;
    }
    public boolean isFuelVesselHasBaseStats(){
        return fuelVesselHasBaseStats;
    }
    public float getFuelVesselEfficiency(){
        return fuelVesselEfficiency;
    }
    public int getFuelVesselHeat(){
        return fuelVesselHeat;
    }
    public int getFuelVesselCriticality(){
        return fuelVesselCriticality;
    }
    public boolean isFuelVesselSelfPriming(){
        return fuelVesselSelfPriming;
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
    public boolean isHeater(){
        return heater;
    }
    public boolean isHeaterHasBaseStats(){
        return heaterHasBaseStats;
    }
    public int getHeaterCooling(){
        return heaterCooling;
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
    public void setController(boolean controller){
        this.controller = controller;
    }
    public void setFuelVessel(boolean fuelVessel){
        this.fuelVessel = fuelVessel;
    }
    public void setFuelVesselHasBaseStats(boolean fuelVesselHasBaseStats){
        this.fuelVesselHasBaseStats = fuelVesselHasBaseStats;
    }
    public void setFuelVesselEfficiency(float fuelVesselEfficiency){
        this.fuelVesselEfficiency = fuelVesselEfficiency;
    }
    public void setFuelVesselHeat(int fuelVesselHeat){
        this.fuelVesselHeat = fuelVesselHeat;
    }
    public void setFuelVesselCriticality(int fuelVesselCriticality){
        this.fuelVesselCriticality = fuelVesselCriticality;
    }
    public void setFuelVesselSelfPriming(boolean fuelVesselSelfPriming){
        this.fuelVesselSelfPriming = fuelVesselSelfPriming;
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
    public void setHeater(boolean heater){
        this.heater = heater;
    }
    public void setHeaterHasBaseStats(boolean heaterHasBaseStats){
        this.heaterHasBaseStats = heaterHasBaseStats;
    }
    public void setHeaterCooling(int heaterCooling){
        this.heaterCooling = heaterCooling;
    }
    public void setSource(boolean source){
        this.source = source;
    }
    public void setSourceEfficiency(float sourceEfficiency){
        this.sourceEfficiency = sourceEfficiency;
    }
}