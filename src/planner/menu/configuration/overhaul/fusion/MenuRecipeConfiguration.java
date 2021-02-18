package planner.menu.configuration.overhaul.fusion;
import multiblock.configuration.overhaul.fusion.Recipe;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuRecipeConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true)).setTooltip("The name of this recipe. This should never change");
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox heat = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox time = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox fluxiness = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Recipe recipe;
    public MenuRecipeConfiguration(GUI gui, Menu parent, Recipe recipe){
        super(gui, parent);
        back.addActionListener((e) -> {
            recipe.name = name.text;
            recipe.efficiency = Float.parseFloat(efficiency.text);
            recipe.heat = Integer.parseInt(heat.text);
            recipe.time = Integer.parseInt(time.text);
            recipe.fluxiness = Float.parseFloat(fluxiness.text);
            gui.open(parent);
        });
        this.recipe = recipe;
    }
    @Override
    public void onGUIOpened(){
        name.text = recipe.name;
        efficiency.text = recipe.efficiency+"";
        heat.text = recipe.heat+"";
        time.text = recipe.time+"";
        fluxiness.text = recipe.fluxiness+"";
    }
    @Override
    public void render(int millisSinceLastTick){
        back.width = name.width = gui.helper.displayWidth();
        efficiency.width = heat.width = time.width = fluxiness.width = gui.helper.displayWidth()*.75;
        efficiency.x = heat.x = time.x = fluxiness.x = gui.helper.displayWidth()*.25;
        efficiency.height = heat.height = time.height = fluxiness.height = name.height = back.height = gui.helper.displayHeight()/16;
        efficiency.y = name.height;
        heat.y = efficiency.y+efficiency.height;
        time.y = heat.y+heat.height;
        fluxiness.y = time.y+time.height;
        back.y = gui.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, efficiency.y, efficiency.x, efficiency.y+efficiency.height, "Efficiency");
        drawText(0, heat.y, heat.x, heat.y+heat.height, "Heat");
        drawText(0, time.y, time.x, time.y+time.height, "Time");
        drawText(0, fluxiness.y, fluxiness.x, fluxiness.y+fluxiness.height, "Fluxiness");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}