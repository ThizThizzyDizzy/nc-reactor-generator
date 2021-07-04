package planner.menu.configuration.overhaul.fissionmsr;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionmsr.Block;
import multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
import multiblock.configuration.overhaul.fissionmsr.PlacementRule;
import planner.Core;
import planner.ImageIO;
import planner.file.FileFormat;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.component.MenuComponentToggleBox;
import planner.menu.configuration.ConfigurationMenu;
import planner.menu.configuration.MenuComponentPlacementRule;
import planner.menu.configuration.MenuPlacementRuleConfiguration;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.image.Image;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuBlockConfiguration extends ConfigurationMenu{
    private final Block block;
    private final MenuComponentMinimalistButton texture, portInputTexture, portOutputTexture, shieldClosedTexture;
    private final MenuComponentMinimalistTextBox name, displayName, portName, portInputDisplayName, portOutputDisplayName, fuelVesselEfficiency, fuelVesselHeat, fuelVesselCriticality, irradiatorEfficiency, irradiatorHeat, reflectorEfficiency, reflectorReflectivity, moderatorFlux, moderatorEfficiency, shieldHeat, shieldEfficiency, heaterCooling, sourceEfficiency;
    private final MenuComponentToggleBox blocksLOS, functional, casing, casingEdge, controller, cluster, conductor, createsCluster, fuelVessel, fuelVesselHasBaseStats, fuelVesselSelfPriming, irradiator, irradiatorHasBaseStats, reflector, reflectorHasBaseStats, moderator, moderatorHasBaseStats, moderatorActive, shield, shieldHasBaseStats, heater, heaterHasBaseStats, source;
    private final MenuComponentLabel legacyNamesLabel, placementRulesLabel, blockRecipesLabel;
    private final MenuComponentMinimaList legacyNames, placementRules, blockRecipes;
    private final MenuComponentMinimalistButton addRule, addBlockRecipe;
    private boolean refreshNeeded = false;
    public MenuBlockConfiguration(GUI gui, Menu parent, Configuration configuration, Block block){
        super(gui, parent, configuration, block.getDisplayName());
        texture = add(new MenuComponentMinimalistButton(sidebar.width, 0, 192, 192, "Set Texture", true, true){
            @Override
            public void render(){
                if(block.texture!=null){
                    Core.applyWhite();
                    drawRect(x, y, x+width, y+height, Core.getTexture(block.texture));
                    return;
                }
                super.render();
            }
            @Override
            public boolean onFilesDropped(double x, double y, String[] files){
                for(String s : files){
                    if(s.endsWith(".png")){
                        try{
                            Image img = ImageIO.read(new File(s));
                            if(img==null)continue;
                            if(img.getWidth()!=img.getHeight()){
                                Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                                continue;
                            }
                            block.setTexture(img);
                        }catch(IOException ex){}
                    }
                }
                return super.onFilesDropped(x, y, files);
            }
        }.setTooltip("Click to change texture\nYou can also drag-and-drop texture files here"));
        name = add(new MenuComponentMinimalistTextBox(texture.x+texture.width, 0, 0, 48, "", true, "Name").setTooltip("The ingame name of this block. Must be namespace:name or namespace:name:metadata\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)"));
        displayName = add(new MenuComponentMinimalistTextBox(name.x, 0, 0, 48, "", true, "Display Name").setTooltip("The user-friendly name of this block."));
        legacyNamesLabel = add(new MenuComponentLabel(name.x, 48, 0, 32, "Legacy Names", true).setTooltip("A list of old names for NCPF back-compatibility"));
        legacyNames = add(new MenuComponentMinimaList(name.x, 48+32, 0, texture.height-legacyNamesLabel.height-name.height, 16));
        portInputTexture = add(new MenuComponentMinimalistButton(sidebar.width, texture.height, 72, 72, "Set Port Texture", true, true){
            @Override
            public void render(){
                if(block.port!=null&&block.port.texture!=null){
                    Core.applyWhite();
                    drawRect(x, y, x+width, y+height, Core.getTexture(block.port.texture));
                    return;
                }
                super.render();
            }
            @Override
            public boolean onFilesDropped(double x, double y, String[] files){
                for(String s : files){
                    if(block.port==null)break;
                    if(s.endsWith(".png")){
                        try{
                            Image img = ImageIO.read(new File(s));
                            if(img==null)continue;
                            if(img.getWidth()!=img.getHeight()){
                                Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                                continue;
                            }
                            block.port.setTexture(img);
                        }catch(IOException ex){}
                    }
                }
                return super.onFilesDropped(x, y, files);
            }
        }.setTooltip("Click to change port input texture\nYou can also drag-and-drop texture files here"));
        portOutputTexture = add(new MenuComponentMinimalistButton(sidebar.width, portInputTexture.y+portInputTexture.height, 72, 72, "Set Port Output Texture", true, true){
            @Override
            public void render(){
                if(block.port!=null&&block.port.portOutputTexture!=null){
                    Core.applyWhite();
                    drawRect(x, y, x+width, y+height, Core.getTexture(block.port.portOutputTexture));
                    return;
                }
                super.render();
            }
            @Override
            public boolean onFilesDropped(double x, double y, String[] files){
                for(String s : files){
                    if(block.port==null)break;
                    if(s.endsWith(".png")){
                        try{
                            Image img = ImageIO.read(new File(s));
                            if(img==null)continue;
                            if(img.getWidth()!=img.getHeight()){
                                Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                                continue;
                            }
                            block.port.setPortOutputTexture(img);
                        }catch(IOException ex){}
                    }
                }
                return super.onFilesDropped(x, y, files);
            }
        }.setTooltip("Click to change port output texture\nYou can also drag-and-drop texture files here"));
        portName = add(new MenuComponentMinimalistTextBox(sidebar.width+portInputTexture.width, portInputTexture.y, 0, 48, "", true, "Port Name").setTooltip("The ingame name of this block's access port. Must be namespace:name or namespace:name:metadata\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)"));
        portInputDisplayName = add(new MenuComponentMinimalistTextBox(sidebar.width+portInputTexture.width, portInputTexture.y+portName.height, 0, 48, "", true, "Port Input Display Name").setTooltip("The user-friendly name of the port in input mode."));
        portOutputDisplayName = add(new MenuComponentMinimalistTextBox(sidebar.width+portInputTexture.width, portInputDisplayName.y+portInputDisplayName.height, 0, 48, "", true, "Port Output Display Name").setTooltip("The user-friendly name of the port in output mode."));
        functional = add(new MenuComponentToggleBox(sidebar.width, portOutputDisplayName.y+portOutputDisplayName.height, 0, 36, "Functional").setTooltip("If set, this block will count against the sparsity penalty"));
        casing = add(new MenuComponentToggleBox(sidebar.width, portOutputDisplayName.y+portOutputDisplayName.height, 0, 36, "Casing").setTooltip("If set, this block can be placed in the multiblock casing walls"));
        casingEdge = add(new MenuComponentToggleBox(sidebar.width, portOutputDisplayName.y+portOutputDisplayName.height, 0, 36, "Casing Edge").setTooltip("If set, this block can be placed in the multiblock casing edge"));
        controller = add(new MenuComponentToggleBox(sidebar.width, portOutputDisplayName.y+portOutputDisplayName.height, 0, 36, "Controller"));
        blocksLOS = add(new MenuComponentToggleBox(sidebar.width, functional.y+functional.height, 0, 36, "Blocks Line of Sight").setTooltip("If true, this block will count against the sparsity penalty"));
        conductor = add(new MenuComponentToggleBox(sidebar.width, functional.y+functional.height, 0, 36, "Conductor").setTooltip("If set, this block will connect clusters to the casing, but will not connect them together"));
        cluster = add(new MenuComponentToggleBox(sidebar.width, functional.y+functional.height, 0, 36, "Can Cluster").setTooltip("If set, this block can be part of a cluster"));
        createsCluster = add(new MenuComponentToggleBox(sidebar.width, functional.y+functional.height, 0, 36, "Creates Cluster").setTooltip("If set, this block will create a cluster"));
        fuelVessel = add(new MenuComponentToggleBox(sidebar.width, createsCluster.y+createsCluster.height, 0, 48, "Fuel Vessel", true));
        fuelVesselHasBaseStats = add(new MenuComponentToggleBox(fuelVessel.x, fuelVessel.y+fuelVessel.height, 0, 32, "Has base stats"));
        fuelVesselEfficiency = add(new MenuComponentMinimalistTextBox(fuelVessel.x, fuelVesselHasBaseStats.y+fuelVesselHasBaseStats.height, 0, 48, "", true, "Efficiency").setFloatFilter());
        fuelVesselHeat = add(new MenuComponentMinimalistTextBox(fuelVessel.x, fuelVesselEfficiency.y+fuelVesselEfficiency.height, 0, 48, "", true, "Heat").setIntFilter());
        fuelVesselCriticality = add(new MenuComponentMinimalistTextBox(fuelVessel.x, fuelVesselHeat.y+fuelVesselHeat.height, 0, 48, "", true, "Criticality").setIntFilter());
        fuelVesselSelfPriming = add(new MenuComponentToggleBox(fuelVessel.x, fuelVesselCriticality.y+fuelVesselCriticality.height, 0, 32, "Self-Priming"));
        source = add(new MenuComponentToggleBox(fuelVessel.x, fuelVesselSelfPriming.y+fuelVesselSelfPriming.height, 0, 48, "Neutron Source", true));
        sourceEfficiency = add(new MenuComponentMinimalistTextBox(source.x, source.y+source.height, 0, 48, "", true, "Efficiency").setFloatFilter());
        moderator = add(new MenuComponentToggleBox(sidebar.width, createsCluster.y+createsCluster.height, 0, 48, "Moderator", true));
        moderatorHasBaseStats = add(new MenuComponentToggleBox(moderator.x, moderator.y+moderator.height, 0, 32, "Has base stats"));
        moderatorEfficiency = add(new MenuComponentMinimalistTextBox(moderator.x, moderatorHasBaseStats.y+moderatorHasBaseStats.height, 0, 48, "", true, "Efficiency").setFloatFilter());
        moderatorFlux = add(new MenuComponentMinimalistTextBox(moderator.x, moderatorEfficiency.y+moderatorEfficiency.height, 0, 48, "", true, "Flux").setIntFilter());
        moderatorActive = add(new MenuComponentToggleBox(moderator.x, moderatorFlux.y+moderatorFlux.height, 0, 32, "Active").setTooltip("If set, this block will be treated as an active moderator for placement rules"));
        shield = add(new MenuComponentToggleBox(moderator.x, moderatorActive.y+moderatorActive.height, 0, 48, "Neutron Shield", true));
        shieldHasBaseStats = add(new MenuComponentToggleBox(shield.x, shield.y+shield.height, 0, 32, "Has base stats"));
        shieldHeat = add(new MenuComponentMinimalistTextBox(shield.x, shieldHasBaseStats.y+shieldHasBaseStats.height, 0, 48, "", true, "Heat per Flux").setIntFilter());
        shieldEfficiency = add(new MenuComponentMinimalistTextBox(shield.x, shieldHeat.y+shieldHeat.height, 0, 48, "", true, "Efficiency").setFloatFilter());
        shieldClosedTexture = add(new MenuComponentMinimalistButton(shield.x, shieldHasBaseStats.y+shieldHasBaseStats.height, 96, 96, "Closed Texture", true, true){
            @Override
            public void render(){
                if(block.shieldClosedTexture!=null){
                    Core.applyWhite();
                    drawRect(x, y, x+width, y+height, Core.getTexture(block.shieldClosedTexture));
                    return;
                }
                super.render();
            }
            @Override
            public boolean onFilesDropped(double x, double y, String[] files){
                for(String s : files){
                    if(s.endsWith(".png")){
                        try{
                            Image img = ImageIO.read(new File(s));
                            if(img==null)continue;
                            if(img.getWidth()!=img.getHeight()){
                                Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                                continue;
                            }
                            block.setShieldClosedTexture(img);
                        }catch(IOException ex){}
                    }
                }
                return super.onFilesDropped(x, y, files);
            }
        }.setTooltip("Click to change closed texture\nYou can also drag-and-drop texture files here"));
        heater = add(new MenuComponentToggleBox(shield.x, shieldEfficiency.y+shieldEfficiency.height, 0, 48, "Heater", true));
        heaterHasBaseStats = add(new MenuComponentToggleBox(heater.x, heater.y+heater.height, 0, 32, "Has base stats"));
        heaterCooling = add(new MenuComponentMinimalistTextBox(heater.x, heaterHasBaseStats.y+heaterHasBaseStats.height, 0, 48, "", true, "Cooling").setIntFilter());
        reflector = add(new MenuComponentToggleBox(sidebar.width, createsCluster.y+createsCluster.height, 0, 48, "Reflector", true));
        reflectorHasBaseStats = add(new MenuComponentToggleBox(reflector.x, reflector.y+reflector.height, 0, 32, "Has base stats"));
        reflectorEfficiency = add(new MenuComponentMinimalistTextBox(reflector.x, reflectorHasBaseStats.y+reflectorHasBaseStats.height, 0, 48, "", true, "Efficiency").setFloatFilter());
        reflectorReflectivity = add(new MenuComponentMinimalistTextBox(reflector.x, reflectorEfficiency.y+reflectorEfficiency.height, 0, 48, "", true, "Reflectivity").setFloatFilter());
        irradiator = add(new MenuComponentToggleBox(sidebar.width, reflectorReflectivity.y+reflectorReflectivity.height, 0, 48, "Irradiator", true));
        irradiatorHasBaseStats = add(new MenuComponentToggleBox(irradiator.x, irradiator.y+irradiator.height, 0, 32, "Has base stats"));
        irradiatorEfficiency = add(new MenuComponentMinimalistTextBox(irradiator.x, irradiatorHasBaseStats.y+irradiatorHasBaseStats.height, 0, 48, "", true, "Efficiency").setFloatFilter());
        irradiatorHeat = add(new MenuComponentMinimalistTextBox(irradiator.x, irradiatorEfficiency.y+irradiatorEfficiency.height, 0, 48, "", true, "Heat").setFloatFilter());
        blockRecipesLabel = add(new MenuComponentLabel(sidebar.width, Math.max(sourceEfficiency.y+sourceEfficiency.height, Math.max(heaterCooling.y+heaterCooling.height, irradiatorHeat.y+irradiatorHeat.height)), 0, 48, "Block Recipes", true));
        blockRecipes = add(new MenuComponentMinimaList(sidebar.width, blockRecipesLabel.y+blockRecipesLabel.height, 0, 0, 16));
        addBlockRecipe = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "New Recipe", true, true));
        placementRulesLabel = add(new MenuComponentLabel(sidebar.width, Math.max(sourceEfficiency.y+sourceEfficiency.height, Math.max(heaterCooling.y+heaterCooling.height, irradiatorHeat.y+irradiatorHeat.height)), 0, 48, "Placement Rules", true));
        placementRules = add(new MenuComponentMinimaList(sidebar.width, placementRulesLabel.y+placementRulesLabel.height, 0, 0, 16));
        addRule = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "New Rule", true, true));
        texture.addActionListener((e) -> {
            try{
                Core.createFileChooser((file) -> {
                    try{
                        Image img = ImageIO.read(file);
                        if(img==null)return;
                        if(img.getWidth()!=img.getHeight()){
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                            return;
                        }
                        block.setTexture(img);
                    }catch(IOException ex){}
                }, FileFormat.PNG);
            }catch(IOException ex){
                Sys.error(ErrorLevel.severe, "Failed to load image!", ex, ErrorCategory.fileIO);
            }
        });
        portInputTexture.addActionListener((e) -> {
            if(block.port==null)return;
            try{
                Core.createFileChooser((file) -> {
                    try{
                        Image img = ImageIO.read(file);
                        if(img==null)return;
                        if(img.getWidth()!=img.getHeight()){
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                            return;
                        }
                        block.port.setTexture(img);
                    }catch(IOException ex){}
                }, FileFormat.PNG);
            }catch(IOException ex){
                Sys.error(ErrorLevel.severe, "Failed to load image!", ex, ErrorCategory.fileIO);
            }
        });
        portOutputTexture.addActionListener((e) -> {
            if(block.port==null)return;
            try{
                Core.createFileChooser((file) -> {
                    try{
                        Image img = ImageIO.read(file);
                        if(img==null)return;
                        if(img.getWidth()!=img.getHeight()){
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                            return;
                        }
                        block.port.setPortOutputTexture(img);
                    }catch(IOException ex){}
                }, FileFormat.PNG);
            }catch(IOException ex){
                Sys.error(ErrorLevel.severe, "Failed to load image!", ex, ErrorCategory.fileIO);
            }
        });
        shieldClosedTexture.addActionListener((e) -> {
            try{
                Core.createFileChooser((file) -> {
                    try{
                        Image img = ImageIO.read(file);
                        if(img==null)return;
                        if(img.getWidth()!=img.getHeight()){
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                            return;
                        }
                        block.setShieldClosedTexture(img);
                    }catch(IOException ex){}
                }, FileFormat.PNG);
            }catch(IOException ex){
                Logger.getLogger(MenuBlockConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        addRule.addActionListener((e) -> {
            PlacementRule rule;
            block.rules.add(rule = new PlacementRule());
            gui.open(new MenuPlacementRuleConfiguration(
                    gui, this, configuration, rule,Core.configuration.overhaul.fissionSFR,
                    multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.values()
            ));
        });
        addBlockRecipe.addActionListener((e) -> {
            BlockRecipe recipe = new BlockRecipe("nuclearcraft:input", "nuclearcraft:output");
            block.allRecipes.add(recipe);
            block.recipes.add(recipe);
            onGUIClosed();
            gui.open(new MenuBlockRecipeConfiguration(gui, this, configuration, block, recipe));
        });
        this.block = block;
    }
    @Override
    public void onGUIOpened(){
        if(block.recipes.size()>0)blockRecipesLabel.text = "Block Recipes ("+block.recipes.size()+")";
        name.text = block.name;
        displayName.text = block.displayName==null?"":block.displayName;
        legacyNames.components.clear();
        for(String s : block.legacyNames){
            legacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, s, true));
        }
        portName.text = block.port==null?"":block.port.name;
        portInputDisplayName.text = block.port==null||block.port.displayName==null?"":block.port.displayName;
        portOutputDisplayName.text = block.port==null||block.port.portOutputDisplayName==null?"":block.port.portOutputDisplayName;
        blocksLOS.isToggledOn = block.blocksLOS;
        functional.isToggledOn = block.functional;
        casing.isToggledOn = block.casing;
        casingEdge.isToggledOn = block.casingEdge;
        controller.isToggledOn = block.controller;
        cluster.isToggledOn = block.cluster;
        conductor.isToggledOn = block.conductor;
        createsCluster.isToggledOn = block.createCluster;
        fuelVessel.isToggledOn = block.fuelVessel;
        fuelVesselHasBaseStats.isToggledOn = block.fuelVesselHasBaseStats;
        fuelVesselEfficiency.text = block.fuelVesselEfficiency+"";
        fuelVesselHeat.text = block.fuelVesselHeat+"";
        fuelVesselCriticality.text = block.fuelVesselCriticality+"";
        fuelVesselSelfPriming.isToggledOn = block.fuelVesselSelfPriming;
        irradiator.isToggledOn = block.irradiator;
        irradiatorHasBaseStats.isToggledOn = block.irradiatorHasBaseStats;
        irradiatorEfficiency.text = block.irradiatorEfficiency+"";
        irradiatorHeat.text = block.irradiatorHeat+"";
        reflector.isToggledOn = block.reflector;
        reflectorHasBaseStats.isToggledOn = block.reflectorHasBaseStats;
        reflectorEfficiency.text = block.reflectorEfficiency+"";
        reflectorReflectivity.text = block.reflectorReflectivity+"";
        moderator.isToggledOn = block.moderator;
        moderatorHasBaseStats.isToggledOn = block.moderatorHasBaseStats;
        moderatorEfficiency.text = block.moderatorEfficiency+"";
        moderatorFlux.text = block.moderatorFlux+"";
        moderatorActive.isToggledOn = block.moderatorActive;
        shield.isToggledOn = block.shield;
        shieldHasBaseStats.isToggledOn = block.shieldHasBaseStats;
        shieldEfficiency.text = block.shieldEfficiency+"";
        shieldHeat.text = block.shieldHeat+"";
        heater.isToggledOn = block.heater;
        heaterHasBaseStats.isToggledOn = block.heaterHasBaseStats;
        heaterCooling.text = block.heaterCooling+"";
        source.isToggledOn = block.source;
        sourceEfficiency.text = block.sourceEfficiency+"";
        placementRules.components.clear();
        for(AbstractPlacementRule<PlacementRule.BlockType, Block> rule : block.rules){
            placementRules.add(new MenuComponentPlacementRule((PlacementRule) rule));
        }
        blockRecipes.components.clear();
        for(BlockRecipe recipe : block.recipes){
            blockRecipes.add(new MenuComponentBlockRecipe(block, recipe));
        }
    }
    @Override
    public void onGUIClosed(){
        block.name = name.text;
        block.displayName = displayName.text.trim().isEmpty()?null:displayName.text;
        block.legacyNames.clear();
        for(MenuComponent c : legacyNames.components){
            if(c instanceof MenuComponentMinimalistTextBox){
                if(((MenuComponentMinimalistTextBox)c).text.trim().isEmpty())continue;
                block.legacyNames.add(((MenuComponentMinimalistTextBox)c).text);
            }
        }
        if(block.allRecipes.isEmpty())block.port = null;
        else if(block.port==null)block.port = new Block("nuclearcraft:port_name");
        if(block.port!=null){
            block.port.name = portName.text;
            block.port.displayName = portInputDisplayName.text.trim().isEmpty()?null:portInputDisplayName.text;
            block.port.portOutputDisplayName = portOutputDisplayName.text.trim().isEmpty()?null:portOutputDisplayName.text;
        }
        block.functional = functional.isToggledOn;
        block.casing = casing.isToggledOn;
        block.casingEdge = casingEdge.isToggledOn;
        block.controller = controller.isToggledOn;
        block.blocksLOS = blocksLOS.isToggledOn;
        block.conductor = conductor.isToggledOn;
        block.cluster = cluster.isToggledOn;
        block.createCluster = createsCluster.isToggledOn;
        block.fuelVessel = fuelVessel.isToggledOn;
        block.reflector = reflector.isToggledOn;
        block.irradiator = irradiator.isToggledOn;
        block.heater = heater.isToggledOn;
        block.moderator = moderator.isToggledOn;
        block.shield = shield.isToggledOn;
        block.source = source.isToggledOn;
        if(block.fuelVessel){
            block.fuelVesselHasBaseStats = fuelVesselHasBaseStats.isToggledOn;
            if(block.fuelVesselHasBaseStats){
                block.fuelVesselEfficiency = Float.parseFloat(fuelVesselEfficiency.text);
                block.fuelVesselHeat = Integer.parseInt(fuelVesselHeat.text);
                block.fuelVesselCriticality = Integer.parseInt(fuelVesselCriticality.text);
                block.fuelVesselSelfPriming = fuelVesselSelfPriming.isToggledOn;
            }else{
                block.fuelVesselEfficiency = 0;
                block.fuelVesselHeat = 0;
                block.fuelVesselCriticality = 0;
                block.fuelVesselSelfPriming = false;
            }
        }else{
            block.fuelVesselHasBaseStats = false;
            block.fuelVesselEfficiency = 0;
            block.fuelVesselHeat = 0;
            block.fuelVesselCriticality = 0;
            block.fuelVesselSelfPriming = false;
        }
        if(block.reflector){
            block.reflectorHasBaseStats = reflectorHasBaseStats.isToggledOn;
            if(block.reflectorHasBaseStats){
                block.reflectorEfficiency = Float.parseFloat(reflectorEfficiency.text);
                block.reflectorReflectivity = Float.parseFloat(reflectorReflectivity.text);
            }else{
                block.reflectorEfficiency = block.reflectorReflectivity = 0;
            }
        }else{
            block.reflectorEfficiency = block.reflectorReflectivity = 0;
            block.reflectorHasBaseStats = false;
        }
        if(block.irradiator){
            block.irradiatorHasBaseStats = irradiatorHasBaseStats.isToggledOn;
            if(block.irradiatorHasBaseStats){
                block.irradiatorEfficiency = Float.parseFloat(irradiatorEfficiency.text);
                block.irradiatorHeat = Float.parseFloat(irradiatorHeat.text);
            }else{
                block.irradiatorEfficiency = block.irradiatorHeat = 0;
            }
        }else{
            block.irradiatorEfficiency = block.irradiatorHeat = 0;
            block.irradiatorHasBaseStats = false;
        }
        if(block.heater){
            block.heaterHasBaseStats = heaterHasBaseStats.isToggledOn;
            if(block.heaterHasBaseStats)block.heaterCooling = Integer.parseInt(heaterCooling.text);
            else block.heaterCooling = 0;
        }else{
            block.heaterCooling = 0;
            block.heaterHasBaseStats = false;
        }
        if(block.moderator){
            block.moderatorHasBaseStats = moderatorHasBaseStats.isToggledOn;
            if(block.moderatorHasBaseStats){
                block.moderatorEfficiency = Float.parseFloat(moderatorEfficiency.text);
                block.moderatorFlux = Integer.parseInt(moderatorFlux.text);
                block.moderatorActive = moderatorActive.isToggledOn;
            }else{
                block.moderatorEfficiency = 0;
                block.moderatorFlux = 0;
                block.moderatorActive = false;
            }
        }else{
            block.moderatorEfficiency = 0;
            block.moderatorFlux = 0;
            block.moderatorHasBaseStats = false;
            block.moderatorActive = false;
        }
        if(block.shield){
            block.shieldHasBaseStats = shieldHasBaseStats.isToggledOn;
            if(block.shieldHasBaseStats){
                block.shieldEfficiency = Float.parseFloat(shieldEfficiency.text);
                block.shieldHeat = Integer.parseInt(shieldHeat.text);
            }else{
                block.shieldEfficiency = 0;
                block.shieldHeat = 0;
            }
        }else{
            block.shieldEfficiency = 0;
            block.shieldHeat = 0;
            block.shieldHasBaseStats = false;
            block.shieldClosedTexture = block.shieldClosedDisplayTexture = null;
        }
        if(block.source)block.sourceEfficiency = Float.parseFloat(sourceEfficiency.text);
        else block.sourceEfficiency = 0;
    }
    @Override
    public void tick(){
        ArrayList<MenuComponent> toRemove = new ArrayList<>();
        boolean hasEmpty = false;
        for(int i = 0; i<legacyNames.components.size(); i++){
            MenuComponent comp = legacyNames.components.get(i);
            if(comp instanceof MenuComponentMinimalistTextBox){
                if(((MenuComponentMinimalistTextBox)comp).text.trim().isEmpty()){
                    if(i==legacyNames.components.size()-1)hasEmpty = true;
                    else toRemove.add(comp);
                }
            }
        }
        if(!hasEmpty)legacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true));
        legacyNames.components.removeAll(toRemove);
        if(refreshNeeded){
            if(block.allRecipes.isEmpty())block.port = null;
            else if(block.port==null)block.port = new Block("nuclearcraft:port_name");
            onGUIOpened();
            refreshNeeded = false;
        }
        super.tick();
    }
    @Override
    public void render(int millisSinceLastTick){
        double w = gui.helper.displayWidth()-texture.width-sidebar.width;
        name.width = displayName.width = w/2;
        displayName.x = name.x+name.width;
        legacyNames.width = legacyNamesLabel.width = w;
        portName.x = portInputDisplayName.x = portOutputDisplayName.x = sidebar.width+portInputTexture.width;
        portName.width = portInputDisplayName.width = portOutputDisplayName.width = gui.helper.displayWidth()-portName.x;
        functional.width = casing.width = casingEdge.width = controller.width = blocksLOS.width = conductor.width = cluster.width = createsCluster.width = (gui.helper.displayWidth()-sidebar.width)/4;
        casing.x = conductor.x = functional.x+functional.width;
        casingEdge.x = cluster.x = casing.x+casing.width;
        controller.x = createsCluster.x = casingEdge.x+casingEdge.width;
        
        fuelVessel.width = fuelVesselHasBaseStats.width = fuelVesselEfficiency.width = fuelVesselHeat.width = fuelVesselCriticality.width = fuelVesselSelfPriming.width = source.width = sourceEfficiency.width = moderator.width = moderatorHasBaseStats.width = moderatorEfficiency.width = moderatorFlux.width = moderatorActive.width = 
                shield.width = heater.width = heaterHasBaseStats.width = heaterCooling.width = reflector.width = reflectorHasBaseStats.width = reflectorEfficiency.width = reflectorReflectivity.width = 
                irradiator.width = irradiatorHasBaseStats.width = irradiatorEfficiency.width = irradiatorHeat.width = (gui.helper.displayWidth()-sidebar.width)/3;
        shieldHasBaseStats.width = shieldEfficiency.width = shieldHeat.width = shield.width-shieldClosedTexture.width;
        
        fuelVessel.x = heater.x = source.x = sidebar.width;//column 1
        reflector.x = moderator.x = sidebar.width+(gui.helper.displayWidth()-sidebar.width)/3;//column 2
        irradiator.x = shield.x = sidebar.width+(gui.helper.displayWidth()-sidebar.width)*2/3;//column 3
        
        fuelVesselHasBaseStats.x = fuelVesselEfficiency.x = fuelVesselHeat.x = fuelVesselCriticality.x = fuelVesselSelfPriming.x = fuelVessel.x;
        moderatorHasBaseStats.x = moderatorEfficiency.x = moderatorFlux.x = moderatorActive.x = moderator.x;
        shieldHasBaseStats.x = shieldHeat.x = shieldEfficiency.x = shield.x;
        heaterHasBaseStats.x = heaterCooling.x = heater.x;
        shieldClosedTexture.x = shield.x+shield.width-shieldClosedTexture.width;
        reflectorHasBaseStats.x = reflectorEfficiency.x = reflectorReflectivity.x = reflector.x;
        irradiatorHasBaseStats.x = irradiatorEfficiency.x = irradiatorHeat.x = irradiator.x;
        
        portInputDisplayName.height = portOutputDisplayName.height = portName.height = block.port==null?0:48;
        portInputTexture.height = portOutputTexture.height = block.port==null?0:72;
        portName.y = portInputTexture.y = texture.y+texture.height;
        portOutputTexture.y = portInputTexture.y+portInputTexture.height;
        portInputDisplayName.y = portName.y+portName.height;
        portOutputDisplayName.y = portInputDisplayName.y+portInputDisplayName.height;
        
        fuelVesselHasBaseStats.height = fuelVessel.isToggledOn?32:0;
        fuelVesselEfficiency.height = fuelVesselHeat.height = fuelVesselCriticality.height = fuelVessel.isToggledOn&&fuelVesselHasBaseStats.isToggledOn?48:0;
        fuelVesselSelfPriming.height = fuelVessel.isToggledOn&&fuelVesselHasBaseStats.isToggledOn?32:0;
        sourceEfficiency.height = source.isToggledOn?48:0;
        moderatorHasBaseStats.height = moderator.isToggledOn?32:0;
        moderatorEfficiency.height = moderatorFlux.height = moderator.isToggledOn&&moderatorHasBaseStats.isToggledOn?48:0;
        moderatorActive.height = moderator.isToggledOn&&moderatorHasBaseStats.isToggledOn?32:0;
        shieldHasBaseStats.height = shield.isToggledOn?32:0;
        shieldClosedTexture.height = shield.isToggledOn?96:0;
        shieldEfficiency.height = shieldHeat.height = shield.isToggledOn&&shieldHasBaseStats.isToggledOn?48:0;
        heaterHasBaseStats.height = heater.isToggledOn?32:0;
        heaterCooling.height = heater.isToggledOn&&heaterHasBaseStats.isToggledOn?48:0;
        reflectorHasBaseStats.height = reflector.isToggledOn?32:0;
        reflectorEfficiency.height = reflectorReflectivity.height = reflector.isToggledOn&&reflectorHasBaseStats.isToggledOn?48:0;
        irradiatorHasBaseStats.height = irradiator.isToggledOn?32:0;
        irradiatorEfficiency.height = irradiatorHeat.height = irradiator.isToggledOn&&irradiatorHasBaseStats.isToggledOn?48:0;
        
        functional.y = casing.y = casingEdge.y = controller.y = portOutputDisplayName.y+portOutputDisplayName.height;
        blocksLOS.y = conductor.y = cluster.y = createsCluster.y = functional.y+functional.height;
        
        fuelVessel.y = blocksLOS.y+blocksLOS.height;
        fuelVesselHasBaseStats.y = fuelVessel.y+fuelVessel.height;
        fuelVesselEfficiency.y = fuelVesselHasBaseStats.y+fuelVesselHasBaseStats.height;
        fuelVesselHeat.y = fuelVesselEfficiency.y+fuelVesselEfficiency.height;
        fuelVesselCriticality.y = fuelVesselHeat.y+fuelVesselHeat.height;
        fuelVesselSelfPriming.y = fuelVesselCriticality.y+fuelVesselCriticality.height;
        
        heater.y = fuelVesselSelfPriming.y+fuelVesselSelfPriming.height;
        heaterHasBaseStats.y = heater.y+heater.height;
        heaterCooling.y = heaterHasBaseStats.y+heaterHasBaseStats.height;
        
        source.y = heaterCooling.y+heaterCooling.height;
        sourceEfficiency.y = source.y+source.height;
        
        reflector.y = blocksLOS.y+blocksLOS.height;
        reflectorHasBaseStats.y = reflector.y+reflector.height;
        reflectorEfficiency.y = reflectorHasBaseStats.y+reflectorHasBaseStats.height;
        reflectorReflectivity.y = reflectorEfficiency.y+reflectorEfficiency.height;
        
        moderator.y = reflectorReflectivity.y+reflectorReflectivity.height;
        moderatorHasBaseStats.y = moderator.y+moderator.height;
        moderatorEfficiency.y = moderatorHasBaseStats.y+moderatorHasBaseStats.height;
        moderatorFlux.y = moderatorEfficiency.y+moderatorEfficiency.height;
        moderatorActive.y = moderatorFlux.y+moderatorFlux.height;
        
        irradiator.y = blocksLOS.y+blocksLOS.height;
        irradiatorHasBaseStats.y = irradiator.y+irradiator.height;
        irradiatorEfficiency.y = irradiatorHasBaseStats.y+irradiatorHasBaseStats.height;
        irradiatorHeat.y = irradiatorEfficiency.y+irradiatorEfficiency.height;
        
        shield.y = irradiatorHeat.y+irradiatorHeat.height;
        shieldHasBaseStats.y = shieldClosedTexture.y = shield.y+shield.height;
        shieldHeat.y = shieldHasBaseStats.y+shieldHasBaseStats.height;
        shieldEfficiency.y = shieldHeat.y+shieldHeat.height;
        
        placementRules.width = placementRulesLabel.width = addRule.width = blockRecipes.width = blockRecipesLabel.width = addBlockRecipe.width = (gui.helper.displayWidth()-sidebar.width)/2;
        blockRecipesLabel.y = placementRulesLabel.y = Math.max(shieldEfficiency.y+shieldEfficiency.height, Math.max(shieldClosedTexture.y+shieldClosedTexture.height, Math.max(moderatorActive.y+moderatorActive.height, sourceEfficiency.y+sourceEfficiency.height)));
        blockRecipes.y = placementRules.y =  blockRecipesLabel.y+blockRecipesLabel.height;
        addRule.y = addBlockRecipe.y = gui.helper.displayHeight()-addRule.height;
        placementRules.height = blockRecipes.height = addRule.y-placementRules.y;
        placementRules.x = addRule.x = placementRulesLabel.x = blockRecipesLabel.x+blockRecipesLabel.width;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : placementRules.components){
            if(c instanceof MenuComponentPlacementRule){
                if(button==((MenuComponentPlacementRule) c).delete){
                    block.rules.remove(((MenuComponentPlacementRule)c).rule);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentPlacementRule) c).edit){
                    gui.open(new MenuPlacementRuleConfiguration(
                            gui, this, configuration,
                            ((MenuComponentPlacementRule<PlacementRule.BlockType, Block, PlacementRule>) c).rule,
                            Core.configuration.overhaul.fissionMSR, PlacementRule.BlockType.values())
                    );
                    return;
                }
            }
        }
        for(simplelibrary.opengl.gui.components.MenuComponent c : blockRecipes.components){
            if(c instanceof MenuComponentBlockRecipe){
                if(button==((MenuComponentBlockRecipe) c).delete){
                    block.allRecipes.remove(((MenuComponentBlockRecipe)c).blockRecipe);
                    block.recipes.remove(((MenuComponentBlockRecipe)c).blockRecipe);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentBlockRecipe) c).edit){
                    onGUIClosed();
                    gui.open(new MenuBlockRecipeConfiguration(gui, this, configuration, block, ((MenuComponentBlockRecipe) c).blockRecipe));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}