package planner.menu.configuration.overhaul.turbine;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.turbine.Block;
import multiblock.configuration.overhaul.turbine.PlacementRule;
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
    private final MenuComponentMinimalistTextBox name, displayName, bladeEfficiency, bladeExpansion, coilEfficiency;
    private final MenuComponentToggleBox blade, bladeStator, coil, bearing, shaft, connector, controller, casing, casingEdge, inlet, outlet;
    private final MenuComponentLabel legacyNamesLabel, placementRulesLabel;
    private final MenuComponentMinimaList legacyNames, placementRules;
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
        bearing = add(new MenuComponentToggleBox(sidebar.width, texture.height, 0, 32, "Bearing", false));
        connector = add(new MenuComponentToggleBox(sidebar.width, texture.height, 0, 32, "Connector", false));
        casing = add(new MenuComponentToggleBox(sidebar.width, texture.height, 0, 32, "Casing", false));
        inlet = add(new MenuComponentToggleBox(sidebar.width, texture.height, 0, 32, "Inlet", false));
        shaft = add(new MenuComponentToggleBox(sidebar.width, texture.height+bearing.height, 0, 32, "Shaft", false));
        controller = add(new MenuComponentToggleBox(sidebar.width, texture.height+bearing.height, 0, 32, "Controller", false));
        casingEdge = add(new MenuComponentToggleBox(sidebar.width, texture.height+bearing.height, 0, 32, "Casing Edge", false));
        outlet = add(new MenuComponentToggleBox(sidebar.width, texture.height+bearing.height, 0, 32, "Outlet", false));
        blade = add(new MenuComponentToggleBox(sidebar.width, shaft.y+shaft.height, 0, 48, "Blade", false, true));
        bladeEfficiency = add(new MenuComponentMinimalistTextBox(blade.x, blade.y+blade.height, 0, 48, "", true, "Efficiency").setFloatFilter());
        bladeExpansion = add(new MenuComponentMinimalistTextBox(blade.x, bladeEfficiency.y+bladeEfficiency.height, 0, 48, "", true, "Expansion").setFloatFilter());
        bladeStator = add(new MenuComponentToggleBox(blade.x, bladeExpansion.y+bladeExpansion.height, 0, 32, "Stator", false));
        coil = add(new MenuComponentToggleBox(sidebar.width, shaft.y+shaft.height, 0, 48, "Coil", false, true));
        coilEfficiency = add(new MenuComponentMinimalistTextBox(coil.x, coil.y+coil.height, 0, 48, "", true, "Efficiency").setFloatFilter());
        placementRulesLabel = add(new MenuComponentLabel(sidebar.width, Math.max(bladeStator.y+bladeStator.height, coilEfficiency.y+coilEfficiency.height), 0, 48, "Placement Rules", true));
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
                    gui, this, configuration, rule,Core.configuration.overhaul.turbine,
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
        bearing.isToggledOn = block.bearing;
        shaft.isToggledOn = block.shaft;
        connector.isToggledOn = block.connector;
        controller.isToggledOn = block.controller;
        casing.isToggledOn = block.casing;
        casingEdge.isToggledOn = block.casingEdge;
        inlet.isToggledOn = block.inlet;
        outlet.isToggledOn = block.outlet;
        blade.isToggledOn = block.blade;
        bladeEfficiency.text = block.bladeEfficiency+"";
        bladeExpansion.text = block.bladeExpansion+"";
        bladeStator.isToggledOn = block.bladeStator;
        coil.isToggledOn = block.coil;
        coilEfficiency.text = block.coilEfficiency+"";
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
        block.bearing = bearing.isToggledOn;
        block.shaft = shaft.isToggledOn;
        block.connector = connector.isToggledOn;
        block.controller = controller.isToggledOn;
        block.casing = casing.isToggledOn;
        block.casingEdge = casingEdge.isToggledOn;
        block.inlet = inlet.isToggledOn;
        block.outlet = outlet.isToggledOn;
        block.blade = blade.isToggledOn;
        if(block.blade){
            block.bladeEfficiency = Float.parseFloat(bladeEfficiency.text);
            block.bladeExpansion = Float.parseFloat(bladeExpansion.text);
            block.bladeStator = bladeStator.isToggledOn;
        }else{
            block.bladeEfficiency = block.bladeExpansion = 0;
            block.bladeStator = false;
        }
        block.coil = coil.isToggledOn;
        if(block.coil)block.coilEfficiency = Float.parseFloat(coilEfficiency.text);
        else block.coilEfficiency = 0;
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
        bearing.width = shaft.width = connector.width = controller.width = casing.width = casingEdge.width = inlet.width = outlet.width = (gui.helper.displayWidth()-sidebar.width)/4;
        connector.x = controller.x = bearing.x+bearing.width;
        casing.x = casingEdge.x = connector.x+connector.width;
        inlet.x = outlet.x = casing.x+casing.width;
        blade.width = bladeEfficiency.width = bladeExpansion.width = bladeStator.width = coil.width = coilEfficiency.width = (gui.helper.displayWidth()-sidebar.width)/2;
        coil.x = coilEfficiency.x = blade.x+blade.width;
        
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
                            Core.configuration.overhaul.turbine, PlacementRule.BlockType.values())
                    );
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}