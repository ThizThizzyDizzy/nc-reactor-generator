package planner.menu.configuration.overhaul.fusion;
import java.util.ArrayList;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fusion.Recipe;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.component.MenuComponentTextureButton;
import planner.menu.configuration.ConfigurationMenu;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuRecipeConfiguration extends ConfigurationMenu{
    private final Recipe recipe;
    private final MenuComponentMinimalistButton inputTexture, outputTexture;
    private final MenuComponentMinimalistTextBox inputName, inputDisplayName, outputName, outputDisplayName, efficiency, heat, time, fluxiness;
    private final MenuComponentLabel inputLegacyNamesLabel;
    private final MenuComponentMinimaList inputLegacyNames;
    public MenuRecipeConfiguration(GUI gui, Menu parent, Configuration configuration, Recipe recipe){
        super(gui, parent, configuration, "Recipe");
        inputTexture = add(new MenuComponentTextureButton(sidebar.width, 0, 192, 192, "Input", true, true, ()->{return recipe.inputTexture;}, recipe::setInputTexture));
        inputName = add(new MenuComponentMinimalistTextBox(inputTexture.x+inputTexture.width, 0, 0, 48, "", true, "Input Name").setTooltip("The ingame name of the recipe input. This should be the name of the fluid itself, not the fluid block or bucket."));
        inputDisplayName = add(new MenuComponentMinimalistTextBox(inputName.x, 0, 0, 48, "", true, "Input Display Name").setTooltip("The user-friendly name of the recipe input."));
        inputLegacyNamesLabel = add(new MenuComponentLabel(inputName.x, 48, 0, 32, "Legacy Names", true).setTooltip("A list of old names for NCPF back-compatibility"));
        inputLegacyNames = add(new MenuComponentMinimaList(inputName.x, 48+32, 0, inputTexture.height-inputLegacyNamesLabel.height-inputName.height, 16));
        outputTexture = add(new MenuComponentTextureButton(sidebar.width, inputTexture.height, 128, 128, "Output", true, true, ()->{return recipe.outputTexture;}, recipe::setOutputTexture));
        outputName = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height, 0, 48, "", true, "Output Name").setTooltip("The ingame name of the recipe output. This should be the name of the fluid itself, not the fluid block or bucket."));
        outputDisplayName = add(new MenuComponentMinimalistTextBox(outputName.x, outputName.y+outputName.height, 0, 48, "", true, "Output Display Name").setTooltip("The user-friendly name of the recipe output."));
        efficiency = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height+outputTexture.height, 0, 48, "", true, "Efficiency").setFloatFilter());
        heat = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height+outputTexture.height, 0, 48, "", true, "Heat").setIntFilter());
        time = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height+outputTexture.height, 0, 48, "", true, "Time").setIntFilter());
        fluxiness = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height+outputTexture.height, 0, 48, "", true, "Fluxiness").setFloatFilter());
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
        efficiency.text = recipe.efficiency+"";
        heat.text = recipe.heat+"";
        time.text = recipe.time+"";
        fluxiness.text = recipe.fluxiness+"";
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
        recipe.efficiency = Integer.parseInt(efficiency.text);
        recipe.heat = Integer.parseInt(heat.text);
        recipe.time = Integer.parseInt(time.text);
        recipe.fluxiness = Integer.parseInt(fluxiness.text);
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
        efficiency.width = heat.width = time.width = fluxiness.width = (gui.helper.displayWidth()-sidebar.width)/4;
        heat.x = efficiency.x+efficiency.width;
        time.x = heat.x+heat.width;
        fluxiness.x = time.x+time.width;
        super.render(millisSinceLastTick);
    }
}