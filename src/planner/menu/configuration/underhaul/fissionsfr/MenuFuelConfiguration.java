package planner.menu.configuration.underhaul.fissionsfr;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuFuelConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true)).setTooltip("The name of this fuel. This should never change");
    private final MenuComponentMinimalistTextBox power = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox heat = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox time = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Fuel fuel;
    public MenuFuelConfiguration(GUI gui, Menu parent, Fuel fuel){
        super(gui, parent);
        back.addActionListener((e) -> {
            fuel.name = name.text;
            fuel.power = Float.parseFloat(power.text);
            fuel.heat = Float.parseFloat(heat.text);
            fuel.time = Integer.parseInt(time.text);
            gui.open(parent);
        });
        this.fuel = fuel;
    }
    @Override
    public void onGUIOpened(){
        name.text = fuel.name;
        power.text = fuel.power+"";
        heat.text = fuel.heat+"";
        time.text = fuel.time+"";
    }
    @Override
    public void render(int millisSinceLastTick){
        back.width = name.width = gui.helper.displayWidth();
        power.width = heat.width = time.width = gui.helper.displayWidth()*.75;
        power.x = heat.x = time.x = gui.helper.displayWidth()*.25;
        power.height = heat.height = time.height = name.height = back.height = gui.helper.displayHeight()/16;
        power.y = name.height;
        heat.y = power.y+power.height;
        time.y = heat.y+heat.height;
        back.y = gui.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, gui.helper.displayHeight()/16, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/16*2, "Power");
        drawText(0, gui.helper.displayHeight()/16*2, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/16*3, "Heat");
        drawText(0, gui.helper.displayHeight()/16*3, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/16*4, "Time");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}