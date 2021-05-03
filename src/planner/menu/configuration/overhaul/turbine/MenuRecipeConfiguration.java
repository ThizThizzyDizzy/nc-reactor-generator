package planner.menu.configuration.overhaul.turbine;
import simplelibrary.image.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import planner.ImageIO;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.turbine.Recipe;
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
public class MenuRecipeConfiguration extends ConfigurationMenu{
    private final Recipe recipe;
    private final MenuComponentMinimalistButton inputTexture, outputTexture;
    private final MenuComponentMinimalistTextBox inputName, inputDisplayName, outputName, outputDisplayName, power, coefficient;
    private final MenuComponentLabel inputLegacyNamesLabel;
    private final MenuComponentMinimaList inputLegacyNames;
    public MenuRecipeConfiguration(GUI gui, Menu parent, Configuration configuration, Recipe recipe){
        super(gui, parent, configuration, "Recipe");
        inputTexture = add(new MenuComponentMinimalistButton(sidebar.width, 0, 192, 192, "Set Input Texture", true, true){
            @Override
            public void render(){
                if(recipe.inputTexture!=null){
                    Core.applyWhite();
                    drawRect(x, y, x+width, y+height, Core.getTexture(recipe.inputTexture));
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
                            recipe.setInputTexture(img);
                        }catch(IOException ex){}
                    }
                }
                return super.onFilesDropped(x, y, files);
            }
        }.setTooltip("Click to change texture\nYou can also drag-and-drop texture files here"));
        inputName = add(new MenuComponentMinimalistTextBox(inputTexture.x+inputTexture.width, 0, 0, 48, "", true, "Input Name").setTooltip("The ingame name of the recipe input. This should be the name of the fluid itself, not the fluid block or bucket."));
        inputDisplayName = add(new MenuComponentMinimalistTextBox(inputName.x, 0, 0, 48, "", true, "Input Display Name").setTooltip("The user-friendly name of the recipe input."));
        inputLegacyNamesLabel = add(new MenuComponentLabel(inputName.x, 48, 0, 32, "Legacy Names", true).setTooltip("A list of old names for NCPF back-compatibility"));
        inputLegacyNames = add(new MenuComponentMinimaList(inputName.x, 48+32, 0, inputTexture.height-inputLegacyNamesLabel.height-inputName.height, 16));
        outputTexture = add(new MenuComponentMinimalistButton(sidebar.width, inputTexture.height, 128, 128, "Set Output Texture", true, true){
            @Override
            public void render(){
                if(recipe.outputTexture!=null){
                    Core.applyWhite();
                    drawRect(x, y, x+width, y+height, Core.getTexture(recipe.outputTexture));
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
                            recipe.setOutputTexture(img);
                        }catch(IOException ex){}
                    }
                }
                return super.onFilesDropped(x, y, files);
            }
        }.setTooltip("Click to change texture\nYou can also drag-and-drop texture files here"));
        outputName = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height, 0, 48, "", true, "Output Name").setTooltip("The ingame name of the recipe output. This should be the name of the fluid itself, not the fluid block or bucket."));
        outputDisplayName = add(new MenuComponentMinimalistTextBox(outputName.x, outputName.y+outputName.height, 0, 48, "", true, "Output Display Name").setTooltip("The user-friendly name of the recipe output."));
        power = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height+outputTexture.height, 0, 48, "", true, "Power").setFloatFilter());
        coefficient = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height+outputTexture.height, 0, 48, "", true, "Expansion Coefficient").setFloatFilter());
        inputTexture.addActionListener((e) -> {
            try{
                Core.createFileChooser((file) -> {
                    try{
                        Image img = ImageIO.read(file);
                        if(img==null)return;
                        if(img.getWidth()!=img.getHeight()){
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                            return;
                        }
                        recipe.setInputTexture(img);
                    }catch(IOException ex){}
                }, FileFormat.PNG);
            }catch(IOException ex){
                Sys.error(ErrorLevel.severe, "Failed to load image!", ex, ErrorCategory.fileIO);
            }
        });
        outputTexture.addActionListener((e) -> {
            try{
                Core.createFileChooser((file) -> {
                    try{
                        Image img = ImageIO.read(file);
                        if(img==null)return;
                        if(img.getWidth()!=img.getHeight()){
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                            return;
                        }
                        recipe.setOutputTexture(img);
                    }catch(IOException ex){}
                }, FileFormat.PNG);
            }catch(IOException ex){
                Sys.error(ErrorLevel.severe, "Failed to load image!", ex, ErrorCategory.fileIO);
            }
        });
        this.recipe = recipe;
    }
    @Override
    public void onGUIOpened(){
        inputName.text = recipe.inputName;
        inputDisplayName.text = recipe.inputDisplayName==null?"":recipe.inputDisplayName;
        inputLegacyNames.components.clear();
        for(String s : recipe.inputLegacyNames){
            inputLegacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, s, true));
        }
        outputName.text = recipe.outputName;
        outputDisplayName.text = recipe.outputDisplayName==null?"":recipe.outputDisplayName;
        power.text = recipe.power+"";
        coefficient.text = recipe.coefficient+"";
    }
    @Override
    public void onGUIClosed(){
        recipe.inputName = inputName.text;
        recipe.inputDisplayName = inputDisplayName.text.trim().isEmpty()?null:inputDisplayName.text;
        recipe.inputLegacyNames.clear();
        for(MenuComponent c : inputLegacyNames.components){
            if(c instanceof MenuComponentMinimalistTextBox){
                if(((MenuComponentMinimalistTextBox)c).text.trim().isEmpty())continue;
                recipe.inputLegacyNames.add(((MenuComponentMinimalistTextBox)c).text);
            }
        }
        recipe.outputName = outputName.text;
        recipe.outputDisplayName = outputDisplayName.text.trim().isEmpty()?null:outputDisplayName.text;
        recipe.power = Float.parseFloat(power.text);
        recipe.coefficient = Float.parseFloat(coefficient.text);
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
        power.width = coefficient.width = (gui.helper.displayWidth()-sidebar.width)/2;
        coefficient.x = power.x+power.width;
        super.render(millisSinceLastTick);
    }
}