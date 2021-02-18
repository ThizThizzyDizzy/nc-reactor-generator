package planner.menu.configuration.overhaul.fissionsfr;
import multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuIrradiatorRecipeConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true)).setTooltip("The name of this irradiator recipe. This should never change");
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox heat = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final IrradiatorRecipe irradiatorRecipe;
    public MenuIrradiatorRecipeConfiguration(GUI gui, Menu parent, IrradiatorRecipe irradiatorRecipe){
        super(gui, parent);
        back.addActionListener((e) -> {
            irradiatorRecipe.name = name.text;
            irradiatorRecipe.efficiency = Float.parseFloat(efficiency.text);
            irradiatorRecipe.heat = Float.parseFloat(heat.text);
            gui.open(parent);
        });
        this.irradiatorRecipe = irradiatorRecipe;
    }
    @Override
    public void onGUIOpened(){
        name.text = irradiatorRecipe.name;
        efficiency.text = irradiatorRecipe.efficiency+"";
        heat.text = irradiatorRecipe.heat+"";
    }
    @Override
    public void render(int millisSinceLastTick){
        back.width = name.width = gui.helper.displayWidth();
        efficiency.width = heat.width = gui.helper.displayWidth()*.75;
        efficiency.x = heat.x = gui.helper.displayWidth()*.25;
        efficiency.height = heat.height = name.height = back.height = gui.helper.displayHeight()/16;
        efficiency.y = name.height;
        heat.y = efficiency.y+efficiency.height;
        back.y = gui.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, gui.helper.displayHeight()/16, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/16*2, "Efficiency");
        drawText(0, gui.helper.displayHeight()/16*2, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/16*3, "Heat");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}