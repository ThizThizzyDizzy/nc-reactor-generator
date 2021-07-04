package planner.menu.configuration.underhaul.fissionsfr;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.underhaul.fissionsfr.Block;
import multiblock.configuration.underhaul.fissionsfr.PlacementRule;
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
    private final MenuComponentMinimalistButton texture;
    private final MenuComponentMinimalistTextBox name, displayName, cooling, active;
    private final MenuComponentToggleBox fuelCell, moderator, casing, controller;
    private final MenuComponentLabel legacyNamesLabel;
    private final MenuComponentMinimaList legacyNames;
    private final MenuComponentLabel placementRulesLabel;
    private final MenuComponentMinimaList placementRules;
    private final MenuComponentMinimalistButton addRule;
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
        fuelCell = add(new MenuComponentToggleBox(sidebar.width, texture.height, 0, 48, "Fuel Cell", false));
        moderator = add(new MenuComponentToggleBox(sidebar.width, texture.height, 0, 48, "Moderator", false));
        cooling = add(new MenuComponentMinimalistTextBox(sidebar.width, texture.height, 0, 48, "", true, "Cooling").setIntFilter());
        casing = add(new MenuComponentToggleBox(sidebar.width, texture.height+fuelCell.height, 0, 48, "Casing", false));
        controller = add(new MenuComponentToggleBox(sidebar.width, texture.height+fuelCell.height, 0, 48, "Controller", false));
        active = add(new MenuComponentMinimalistTextBox(sidebar.width, texture.height+fuelCell.height, 0, 48, "", true, "Active Coolant").setTooltip("If set, this block is an active cooler\nThis is the fluid it takes an an input"));
        placementRulesLabel = add(new MenuComponentLabel(sidebar.width, active.y+active.height, 0, 48, "Placement Rules", true));
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
        addRule.addActionListener((e) -> {
            PlacementRule rule;
            block.rules.add(rule = new PlacementRule());
            gui.open(new MenuPlacementRuleConfiguration(
                    gui, this, configuration, rule,Core.configuration.underhaul.fissionSFR,
                    multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.values()
            ));
        });
        this.block = block;
    }
    @Override
    public void onGUIOpened(){
        name.text = block.name;
        displayName.text = block.displayName==null?"":block.displayName;
        legacyNames.components.clear();
        for(String s : block.legacyNames){
            legacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, s, true));
        }
        fuelCell.isToggledOn = block.fuelCell;
        moderator.isToggledOn = block.moderator;
        cooling.text = block.cooling+"";
        casing.isToggledOn = block.casing;
        controller.isToggledOn = block.controller;
        active.text = block.active==null?"":block.active;
        placementRules.components.clear();
        for(AbstractPlacementRule<PlacementRule.BlockType, Block> rule : block.rules){
            placementRules.add(new MenuComponentPlacementRule((PlacementRule) rule));
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
        block.fuelCell = fuelCell.isToggledOn;
        block.moderator = moderator.isToggledOn;
        block.cooling = Integer.parseInt(cooling.text);
        block.casing = casing.isToggledOn;
        block.controller = controller.isToggledOn;
        block.active = active.text.trim().isEmpty()?null:active.text;
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
        fuelCell.width = moderator.width = cooling.width = casing.width = controller.width = active.width = (w+texture.width)/3;
        casing.y = controller.y = active.y = fuelCell.y+fuelCell.height;
        controller.x = moderator.x = fuelCell.x+fuelCell.width;
        cooling.x = active.x = moderator.x+moderator.width;
        placementRules.width = placementRulesLabel.width = addRule.width = gui.helper.displayWidth()-sidebar.width;
        addRule.y = gui.helper.displayHeight()-addRule.height;
        placementRules.height = addRule.y-placementRules.y;
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
                            Core.configuration.underhaul.fissionSFR, PlacementRule.BlockType.values())
                    );
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}