package planner.menu.configuration.overhaul.turbine;
import planner.Core;
import multiblock.configuration.overhaul.turbine.Recipe;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import planner.menu.Menu;
public class MenuRecipeConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true)).setTooltip("The name of this recipe. This should never change");
    private final MenuComponentMinimalistTextBox input = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Input", true));
    private final MenuComponentMinimalistTextBox output = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Output", true));
    private final MenuComponentMinimalistTextBox coefficient = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox power = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Recipe recipe;
    public MenuRecipeConfiguration(GUI gui, Menu parent, Recipe recipe){
        super(gui, parent);
        back.addActionListener((e) -> {
            recipe.name = name.text;
            recipe.input = input.text;
            recipe.output = output.text;
            recipe.coefficient = Float.parseFloat(coefficient.text);
            recipe.power = Float.parseFloat(power.text);
            gui.open(parent);
        });
        this.recipe = recipe;
    }
    @Override
    public void onGUIOpened(){
        name.text = recipe.name;
        input.text = recipe.input;
        output.text = recipe.output;
        coefficient.text = recipe.coefficient+"";
        power.text = recipe.power+"";
    }
    @Override
    public void render(int millisSinceLastTick){
        back.width = name.width = Core.helper.displayWidth();
        coefficient.width = input.width = output.width = power.width = Core.helper.displayWidth()*.75;
        coefficient.x = input.x = output.x = power.x = Core.helper.displayWidth()*.25;
        coefficient.height = input.height = output.height = power.height = name.height = back.height = Core.helper.displayHeight()/16;
        input.y = name.height;
        output.y = input.y+input.height;
        coefficient.y = output.y+output.height;
        power.y = coefficient.y+coefficient.height;
        back.y = Core.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, Core.helper.displayHeight()/16, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/16*2, "Input");
        drawText(0, Core.helper.displayHeight()/16*2, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/16*3, "Output");
        drawText(0, Core.helper.displayHeight()/16*3, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/16*4, "Coefficient");
        drawText(0, Core.helper.displayHeight()/16*4, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/16*5, "Power");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}