package planner.menu.configuration.underhaul.fissionsfr;
import planner.Core;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import planner.menu.Menu;
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
        back.width = name.width = Core.helper.displayWidth();
        power.width = heat.width = time.width = Core.helper.displayWidth()*.75;
        power.x = heat.x = time.x = Core.helper.displayWidth()*.25;
        power.height = heat.height = time.height = name.height = back.height = Core.helper.displayHeight()/16;
        power.y = name.height;
        heat.y = power.y+power.height;
        time.y = heat.y+heat.height;
        back.y = Core.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, Core.helper.displayHeight()/16, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/16*2, "Power");
        drawText(0, Core.helper.displayHeight()/16*2, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/16*3, "Heat");
        drawText(0, Core.helper.displayHeight()/16*3, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/16*4, "Time");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}