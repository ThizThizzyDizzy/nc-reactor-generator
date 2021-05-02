package planner.menu.configuration.overhaul.fissionsfr;
import simplelibrary.image.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import planner.ImageIO;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionsfr.Block;
import multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
import planner.Core;
import planner.Main;
import planner.file.FileFormat;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.component.MenuComponentToggleBox;
import planner.menu.configuration.ConfigurationMenu;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuBlockRecipeConfiguration extends ConfigurationMenu{
    private final Block block;
    private final BlockRecipe blockRecipe;
    private final MenuComponentMinimalistButton inputTexture, outputTexture;
    private final MenuComponentMinimalistTextBox inputName, inputDisplayName, outputName, outputDisplayName, inputRate, outputRate,
            fuelCellEfficiency, fuelCellHeat, fuelCellTime, fuelCellCriticality, moderatorEfficiency, moderatorFlux, shieldHeat, shieldEfficiency, heatsinkCooling, reflectorEfficiency, reflectorReflectivity, irradiatorHeat, irradiatorEfficiency;
    private final MenuComponentToggleBox fuelCellSelfPriming, moderatorActive;
    private final MenuComponentLabel inputLegacyNamesLabel, fuelCell, moderator, shield, heatsink, reflector, irradiator;
    private final MenuComponentMinimaList inputLegacyNames;
    public MenuBlockRecipeConfiguration(GUI gui, Menu parent, Configuration configuration, Block block, BlockRecipe blockRecipe){
        super(gui, parent, configuration, "Block Recipe");
        inputTexture = add(new MenuComponentMinimalistButton(sidebar.width, 0, 192, 192, "Set Input Texture", true, true){
            @Override
            public void render(){
                if(blockRecipe.inputTexture!=null){
                    Core.applyWhite();
                    drawRect(x, y, x+width, y+height, Core.getTexture(blockRecipe.inputTexture));
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
                                if(Main.hasAWT){
                                    javax.swing.JOptionPane.showMessageDialog(null, "Image is not square!", "Error loading image", javax.swing.JOptionPane.ERROR_MESSAGE);
                                }else{
                                    Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                                }
                                continue;
                            }
                            blockRecipe.setInputTexture(img);
                        }catch(IOException ex){}
                    }
                }
                return super.onFilesDropped(x, y, files);
            }
        }.setTooltip("Click to change texture\nYou can also drag-and-drop texture files here"));
        inputName = add(new MenuComponentMinimalistTextBox(inputTexture.x+inputTexture.width, 0, 0, 48, "", true, "Input Name").setTooltip("The ingame name of the block recipe input. This should be the name of the fluid itself, not the fluid block or bucket."));
        inputDisplayName = add(new MenuComponentMinimalistTextBox(inputName.x, 0, 0, 48, "", true, "Input Display Name").setTooltip("The user-friendly name of the block recipe input."));
        inputLegacyNamesLabel = add(new MenuComponentLabel(inputName.x, 48, 0, 32, "Legacy Names", true).setTooltip("A list of old names for NCPF back-compatibility"));
        inputLegacyNames = add(new MenuComponentMinimaList(inputName.x, 48+32, 0, inputTexture.height-inputLegacyNamesLabel.height-inputName.height, 16));
        outputTexture = add(new MenuComponentMinimalistButton(sidebar.width, inputTexture.height, 128, 128, "Set Output Texture", true, true){
            @Override
            public void render(){
                if(blockRecipe.outputTexture!=null){
                    Core.applyWhite();
                    drawRect(x, y, x+width, y+height, Core.getTexture(blockRecipe.outputTexture));
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
                                if(Main.hasAWT){
                                    javax.swing.JOptionPane.showMessageDialog(null, "Image is not square!", "Error loading image", javax.swing.JOptionPane.ERROR_MESSAGE);
                                }else{
                                    Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                                }
                                continue;
                            }
                            blockRecipe.setOutputTexture(img);
                        }catch(IOException ex){}
                    }
                }
                return super.onFilesDropped(x, y, files);
            }
        }.setTooltip("Click to change texture\nYou can also drag-and-drop texture files here"));
        outputName = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height, 0, 48, "", true, "Output Name").setTooltip("The ingame name of the block recipe output. This should be the name of the fluid itself, not the fluid block or bucket."));
        outputDisplayName = add(new MenuComponentMinimalistTextBox(outputName.x, outputName.y+outputName.height, 0, 48, "", true, "Output Display Name").setTooltip("The user-friendly name of the block recipe output."));
        inputRate = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height+outputTexture.height, 0, 48, "", true, "Input Rate").setIntFilter());
        outputRate = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height+outputTexture.height, 0, 48, "", true, "Output Rate").setIntFilter());
        
        fuelCell = add(new MenuComponentLabel(sidebar.width, inputRate.y+inputRate.height, 0, block.fuelCell?48:0, "Fuel Cell", true));
        fuelCellEfficiency = add(new MenuComponentMinimalistTextBox(fuelCell.x, fuelCell.y+fuelCell.height, 0, block.fuelCell?48:0, "", true, "Efficiency").setFloatFilter());
        fuelCellHeat = add(new MenuComponentMinimalistTextBox(fuelCell.x, fuelCellEfficiency.y+fuelCellEfficiency.height, 0, block.fuelCell?48:0, "", true, "Heat").setIntFilter());
        fuelCellTime = add(new MenuComponentMinimalistTextBox(fuelCell.x, fuelCellHeat.y+fuelCellHeat.height, 0, block.fuelCell?48:0, "", true, "Time").setIntFilter());
        fuelCellCriticality = add(new MenuComponentMinimalistTextBox(fuelCell.x, fuelCellTime.y+fuelCellTime.height, 0, block.fuelCell?48:0, "", true, "Criticality").setIntFilter());
        fuelCellSelfPriming = add(new MenuComponentToggleBox(fuelCell.x, fuelCellCriticality.y+fuelCellCriticality.height, 0, block.fuelCell?32:0, "Self-Priming", false));
        heatsink = add(new MenuComponentLabel(fuelCell.x, fuelCellSelfPriming.y+fuelCellSelfPriming.height, 0, block.heatsink?48:0, "Heatsink", true));
        heatsinkCooling = add(new MenuComponentMinimalistTextBox(heatsink.x, heatsink.y+heatsink.height, 0, block.heatsink?48:0, "", true, "Cooling").setIntFilter());
        
        reflector = add(new MenuComponentLabel(sidebar.width, inputRate.y+inputRate.height, 0, block.reflector?48:0, "Reflector", true));
        reflectorEfficiency = add(new MenuComponentMinimalistTextBox(reflector.x, reflector.y+reflector.height, 0, block.reflector?48:0, "", true, "Efficiency").setFloatFilter());
        reflectorReflectivity = add(new MenuComponentMinimalistTextBox(reflector.x, reflectorEfficiency.y+reflectorEfficiency.height, 0, block.reflector?48:0, "", true, "Reflectivity").setFloatFilter());
        moderator = add(new MenuComponentLabel(sidebar.width, reflectorReflectivity.y+reflectorReflectivity.height, 0, block.moderator?48:0, "Moderator", true));
        moderatorEfficiency = add(new MenuComponentMinimalistTextBox(moderator.x, moderator.y+moderator.height, 0, block.moderator?48:0, "", true, "Efficiency").setFloatFilter());
        moderatorFlux = add(new MenuComponentMinimalistTextBox(moderator.x, moderatorEfficiency.y+moderatorEfficiency.height, 0, block.moderator?48:0, "", true, "Flux").setIntFilter());
        moderatorActive = add(new MenuComponentToggleBox(moderator.x, moderatorFlux.y+moderatorFlux.height, 0, block.moderator?32:0, "Active", false).setTooltip("If set, this block will be treated as an active moderator for placement rules"));
        
        irradiator = add(new MenuComponentLabel(sidebar.width, inputRate.y+inputRate.height, 0, block.irradiator?48:0, "Irradiator", true));
        irradiatorEfficiency = add(new MenuComponentMinimalistTextBox(irradiator.x, irradiator.y+irradiator.height, 0, block.irradiator?48:0, "", true, "Efficiency").setFloatFilter());
        irradiatorHeat = add(new MenuComponentMinimalistTextBox(irradiator.x, irradiatorEfficiency.y+irradiatorEfficiency.height, 0, block.irradiator?48:0, "", true, "Heat").setFloatFilter());
        shield = add(new MenuComponentLabel(moderator.x, irradiatorHeat.y+irradiatorHeat.height, 0, block.shield?48:0, "Neutron Shield", true));
        shieldHeat = add(new MenuComponentMinimalistTextBox(shield.x, shield.y+shield.height, 0, block.shield?48:0, "", true, "Heat per Flux").setIntFilter());
        shieldEfficiency = add(new MenuComponentMinimalistTextBox(shield.x, shieldHeat.y+shieldHeat.height, 0, block.shield?48:0, "", true, "Efficiency").setFloatFilter());
        
        inputTexture.addActionListener((e) -> {
            Core.createFileChooser((file, format) -> {
                try{
                    Image img = ImageIO.read(file);
                    if(img==null)return;
                    if(img.getWidth()!=img.getHeight()){
                        if(Main.hasAWT){
                            javax.swing.JOptionPane.showMessageDialog(null, "Image is not square!", "Error loading image", javax.swing.JOptionPane.ERROR_MESSAGE);
                        }else{
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                        }
                        return;
                    }
                    blockRecipe.setInputTexture(img);
                }catch(IOException ex){}
            }, FileFormat.PNG);
        });
        outputTexture.addActionListener((e) -> {
            Core.createFileChooser((file, format) -> {
                try{
                    Image img = ImageIO.read(file);
                    if(img==null)return;
                    if(img.getWidth()!=img.getHeight()){
                        if(Main.hasAWT){
                            javax.swing.JOptionPane.showMessageDialog(null, "Image is not square!", "Error loading image", javax.swing.JOptionPane.ERROR_MESSAGE);
                        }else{
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                        }
                        return;
                    }
                    blockRecipe.setOutputTexture(img);
                }catch(IOException ex){}
            }, FileFormat.PNG);
        });
        this.block = block;
        this.blockRecipe = blockRecipe;
    }
    @Override
    public void onGUIOpened(){
        inputName.text = blockRecipe.inputName;
        inputDisplayName.text = blockRecipe.inputDisplayName==null?"":blockRecipe.inputDisplayName;
        inputLegacyNames.components.clear();
        for(String s : blockRecipe.inputLegacyNames){
            inputLegacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, s, true));
        }
        outputName.text = blockRecipe.outputName;
        outputDisplayName.text = blockRecipe.outputDisplayName==null?"":blockRecipe.outputDisplayName;
        inputRate.text = blockRecipe.inputRate+"";
        outputRate.text = blockRecipe.outputRate+"";
        fuelCellEfficiency.text = blockRecipe.fuelCellEfficiency+"";
        fuelCellHeat.text = blockRecipe.fuelCellHeat+"";
        fuelCellTime.text = blockRecipe.fuelCellTime+"";
        fuelCellCriticality.text = blockRecipe.fuelCellCriticality+"";
        fuelCellSelfPriming.isToggledOn = blockRecipe.fuelCellSelfPriming;
        irradiatorEfficiency.text = blockRecipe.irradiatorEfficiency+"";
        irradiatorHeat.text = blockRecipe.irradiatorHeat+"";
        reflectorEfficiency.text = blockRecipe.reflectorEfficiency+"";
        reflectorReflectivity.text = blockRecipe.reflectorReflectivity+"";
        moderatorEfficiency.text = blockRecipe.moderatorEfficiency+"";
        moderatorFlux.text = blockRecipe.moderatorFlux+"";
        moderatorActive.isToggledOn = blockRecipe.moderatorActive;
        shieldEfficiency.text = blockRecipe.shieldEfficiency+"";
        shieldHeat.text = blockRecipe.shieldHeat+"";
        heatsinkCooling.text = blockRecipe.heatsinkCooling+"";
    }
    @Override
    public void onGUIClosed(){
        blockRecipe.inputName = inputName.text;
        blockRecipe.inputDisplayName = inputDisplayName.text.trim().isEmpty()?null:inputDisplayName.text;
        blockRecipe.inputLegacyNames.clear();
        for(MenuComponent c : inputLegacyNames.components){
            if(c instanceof MenuComponentMinimalistTextBox){
                if(((MenuComponentMinimalistTextBox)c).text.trim().isEmpty())continue;
                blockRecipe.inputLegacyNames.add(((MenuComponentMinimalistTextBox)c).text);
            }
        }
        blockRecipe.outputName = outputName.text;
        blockRecipe.outputDisplayName = outputDisplayName.text.trim().isEmpty()?null:outputDisplayName.text;
        blockRecipe.inputRate = Integer.parseInt(inputRate.text);
        blockRecipe.outputRate = Integer.parseInt(outputRate.text);
        if(block.fuelCell){
            blockRecipe.fuelCellEfficiency = Float.parseFloat(fuelCellEfficiency.text);
            blockRecipe.fuelCellHeat = Integer.parseInt(fuelCellHeat.text);
            blockRecipe.fuelCellTime = Integer.parseInt(fuelCellTime.text);
            blockRecipe.fuelCellCriticality = Integer.parseInt(fuelCellCriticality.text);
            blockRecipe.fuelCellSelfPriming = fuelCellSelfPriming.isToggledOn;
        }
        if(block.moderator){
            blockRecipe.moderatorEfficiency = Float.parseFloat(moderatorEfficiency.text);
            blockRecipe.moderatorFlux = Integer.parseInt(moderatorFlux.text);
            blockRecipe.moderatorActive = moderatorActive.isToggledOn;
        }
        if(block.shield){
            blockRecipe.shieldEfficiency = Float.parseFloat(shieldEfficiency.text);
            blockRecipe.shieldHeat = Integer.parseInt(shieldHeat.text);
        }
        if(block.heatsink){
            blockRecipe.heatsinkCooling = Integer.parseInt(heatsinkCooling.text);
        }
        if(block.reflector){
            blockRecipe.reflectorEfficiency = Float.parseFloat(reflectorEfficiency.text);
            blockRecipe.reflectorReflectivity = Float.parseFloat(reflectorReflectivity.text);
        }
        if(block.irradiator){
            blockRecipe.irradiatorEfficiency = Float.parseFloat(irradiatorEfficiency.text);
            blockRecipe.irradiatorHeat = Float.parseFloat(irradiatorHeat.text);
        }
    }
    @Override
    public void tick(){
        ArrayList<MenuComponent> toRemove = new ArrayList<>();
        boolean hasEmpty = false;
        for(int i = 0; i<inputLegacyNames.components.size(); i++){
            MenuComponent comp = inputLegacyNames.components.get(i);
            if(comp instanceof MenuComponentMinimalistTextBox){
                if(((MenuComponentMinimalistTextBox)comp).text.trim().isEmpty()){
                    if(i==inputLegacyNames.components.size()-1)hasEmpty = true;
                    else toRemove.add(comp);
                }
            }
        }
        if(!hasEmpty)inputLegacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true));
        inputLegacyNames.components.removeAll(toRemove);
        super.tick();
    }
    @Override
    public void render(int millisSinceLastTick){
        double w = gui.helper.displayWidth()-inputTexture.width-sidebar.width;
        inputName.width = inputDisplayName.width = w/2;
        inputDisplayName.x = inputName.x+inputName.width;
        inputLegacyNames.width = inputLegacyNamesLabel.width = w;
        outputTexture.x = gui.helper.displayWidth()-outputTexture.width;
        outputName.width = outputDisplayName.width = gui.helper.displayWidth()-outputTexture.width-sidebar.width;
        inputRate.width = outputRate.width = (gui.helper.displayWidth()-sidebar.width)/2;
        outputRate.x = inputRate.x+inputRate.width;
        
        fuelCell.width = fuelCellEfficiency.width = fuelCellHeat.width = fuelCellTime.width = fuelCellCriticality.width = fuelCellSelfPriming.width = moderator.width = moderatorEfficiency.width = moderatorFlux.width = moderatorActive.width = 
                shield.width = heatsink.width = heatsinkCooling.width = reflector.width = reflectorEfficiency.width = reflectorReflectivity.width = 
                irradiator.width = irradiatorEfficiency.width = irradiatorHeat.width = shieldEfficiency.width = shieldHeat.width = (gui.helper.displayWidth()-sidebar.width)/3;

        fuelCell.x = heatsink.x = sidebar.width;//column 1
        reflector.x = moderator.x = sidebar.width+(gui.helper.displayWidth()-sidebar.width)/3;//column 2
        irradiator.x = shield.x = sidebar.width+(gui.helper.displayWidth()-sidebar.width)*2/3;//column 3
        
        fuelCellEfficiency.x = fuelCellHeat.x = fuelCellTime.x = fuelCellCriticality.x = fuelCellSelfPriming.x = fuelCell.x;
        moderatorEfficiency.x = moderatorFlux.x = moderatorActive.x = moderator.x;
        shieldHeat.x = shieldEfficiency.x = shield.x;
        heatsinkCooling.x = heatsink.x;
        reflectorEfficiency.x = reflectorReflectivity.x = reflector.x;
        irradiatorEfficiency.x = irradiatorHeat.x = irradiator.x;
        super.render(millisSinceLastTick);
    }
}