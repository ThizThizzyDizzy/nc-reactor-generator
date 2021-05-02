package planner.menu.configuration.overhaul.fissionsfr;
import simplelibrary.image.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import planner.ImageIO;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import planner.Core;
import planner.Main;
import planner.file.FileFormat;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.ConfigurationMenu;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuCoolantRecipeConfiguration extends ConfigurationMenu{
    private final CoolantRecipe coolantRecipe;
    private final MenuComponentMinimalistButton inputTexture, outputTexture;
    private final MenuComponentMinimalistTextBox inputName, inputDisplayName, outputName, outputDisplayName, outputRatio, heat;
    private final MenuComponentLabel inputLegacyNamesLabel;
    private final MenuComponentMinimaList inputLegacyNames;
    public MenuCoolantRecipeConfiguration(GUI gui, Menu parent, Configuration configuration, CoolantRecipe coolantRecipe){
        super(gui, parent, configuration, "Coolant Recipe");
        inputTexture = add(new MenuComponentMinimalistButton(sidebar.width, 0, 192, 192, "Set Input Texture", true, true){
            @Override
            public void render(){
                if(coolantRecipe.inputTexture!=null){
                    Core.applyWhite();
                    drawRect(x, y, x+width, y+height, Core.getTexture(coolantRecipe.inputTexture));
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
                            coolantRecipe.setInputTexture(img);
                        }catch(IOException ex){}
                    }
                }
                return super.onFilesDropped(x, y, files);
            }
        }.setTooltip("Click to change texture\nYou can also drag-and-drop texture files here"));
        inputName = add(new MenuComponentMinimalistTextBox(inputTexture.x+inputTexture.width, 0, 0, 48, "", true, "Input Name").setTooltip("The ingame name of the coolant recipe input. This should be the name of the fluid itself, not the fluid block or bucket."));
        inputDisplayName = add(new MenuComponentMinimalistTextBox(inputName.x, 0, 0, 48, "", true, "Input Display Name").setTooltip("The user-friendly name of the coolant recipe input."));
        inputLegacyNamesLabel = add(new MenuComponentLabel(inputName.x, 48, 0, 32, "Legacy Names", true).setTooltip("A list of old names for NCPF back-compatibility"));
        inputLegacyNames = add(new MenuComponentMinimaList(inputName.x, 48+32, 0, inputTexture.height-inputLegacyNamesLabel.height-inputName.height, 16));
        outputTexture = add(new MenuComponentMinimalistButton(sidebar.width, inputTexture.height, 128, 128, "Set Output Texture", true, true){
            @Override
            public void render(){
                if(coolantRecipe.outputTexture!=null){
                    Core.applyWhite();
                    drawRect(x, y, x+width, y+height, Core.getTexture(coolantRecipe.outputTexture));
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
                            coolantRecipe.setOutputTexture(img);
                        }catch(IOException ex){}
                    }
                }
                return super.onFilesDropped(x, y, files);
            }
        }.setTooltip("Click to change texture\nYou can also drag-and-drop texture files here"));
        outputName = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height, 0, 48, "", true, "Output Name").setTooltip("The ingame name of the coolant recipe output. This should be the name of the fluid itself, not the fluid block or bucket."));
        outputDisplayName = add(new MenuComponentMinimalistTextBox(outputName.x, outputName.y+outputName.height, 0, 48, "", true, "Output Display Name").setTooltip("The user-friendly name of the coolant recipe output."));
        outputRatio = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height+outputTexture.height, 0, 48, "", true, "Output Ratio").setFloatFilter());
        heat = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height+outputTexture.height, 0, 48, "", true, "Heat").setIntFilter());
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
                    coolantRecipe.setInputTexture(img);
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
                    coolantRecipe.setOutputTexture(img);
                }catch(IOException ex){}
            }, FileFormat.PNG);
        });
        this.coolantRecipe = coolantRecipe;
    }
    @Override
    public void onGUIOpened(){
        inputName.text = coolantRecipe.inputName;
        inputDisplayName.text = coolantRecipe.inputDisplayName==null?"":coolantRecipe.inputDisplayName;
        inputLegacyNames.components.clear();
        for(String s : coolantRecipe.inputLegacyNames){
            inputLegacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, s, true));
        }
        outputName.text = coolantRecipe.outputName;
        outputDisplayName.text = coolantRecipe.outputDisplayName==null?"":coolantRecipe.outputDisplayName;
        outputRatio.text = coolantRecipe.outputRatio+"";
        heat.text = coolantRecipe.heat+"";
    }
    @Override
    public void onGUIClosed(){
        coolantRecipe.inputName = inputName.text;
        coolantRecipe.inputDisplayName = inputDisplayName.text.trim().isEmpty()?null:inputDisplayName.text;
        coolantRecipe.inputLegacyNames.clear();
        for(MenuComponent c : inputLegacyNames.components){
            if(c instanceof MenuComponentMinimalistTextBox){
                if(((MenuComponentMinimalistTextBox)c).text.trim().isEmpty())continue;
                coolantRecipe.inputLegacyNames.add(((MenuComponentMinimalistTextBox)c).text);
            }
        }
        coolantRecipe.outputName = outputName.text;
        coolantRecipe.outputDisplayName = outputDisplayName.text.trim().isEmpty()?null:outputDisplayName.text;
        coolantRecipe.outputRatio = Float.parseFloat(outputRatio.text);
        coolantRecipe.heat = Integer.parseInt(heat.text);
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
        outputRatio.width = heat.width = (gui.helper.displayWidth()-sidebar.width)/2;
        heat.x = outputRatio.x+outputRatio.width;
        super.render(millisSinceLastTick);
    }
}