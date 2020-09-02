package planner.menu.configuration.overhaul.fissionmsr;
import planner.Core;
import multiblock.configuration.overhaul.fissionmsr.Fuel;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuFuelConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true));
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox heat = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox time = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox criticality = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistOptionButton selfPriming = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Self Prming", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Fuel fuel;
    public MenuFuelConfiguration(GUI gui, Menu parent, Fuel fuel){
        super(gui, parent);
        back.addActionListener((e) -> {
            fuel.name = name.text;
            fuel.efficiency = Float.parseFloat(efficiency.text);
            fuel.heat = Integer.parseInt(heat.text);
            fuel.time = Integer.parseInt(time.text);
            fuel.criticality = Integer.parseInt(criticality.text);
            fuel.selfPriming = selfPriming.getIndex()==1;
            gui.open(parent);
        });
        this.fuel = fuel;
    }
    @Override
    public void onGUIOpened(){
        name.text = fuel.name;
        efficiency.text = fuel.efficiency+"";
        heat.text = fuel.heat+"";
        time.text = fuel.time+"";
        criticality.text = fuel.criticality+"";
        selfPriming.setIndex(fuel.selfPriming?1:0);
    }
    @Override
    public void render(int millisSinceLastTick){
        back.width = name.width = selfPriming.width = Core.helper.displayWidth();
        efficiency.width = heat.width = time.width = criticality.width = Core.helper.displayWidth()*.75;
        efficiency.x = heat.x = time.x = criticality.x = Core.helper.displayWidth()*.25;
        efficiency.height = heat.height = time.height = criticality.height = selfPriming.height = name.height = back.height = Core.helper.displayHeight()/16;
        efficiency.y = name.height;
        heat.y = efficiency.y+efficiency.height;
        time.y = heat.y+heat.height;
        criticality.y = time.y+time.height;
        selfPriming.y = criticality.y+criticality.height;
        back.y = Core.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, Core.helper.displayHeight()/16, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/16*2, "Power");
        drawText(0, Core.helper.displayHeight()/16*2, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/16*3, "Heat");
        drawText(0, Core.helper.displayHeight()/16*3, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/16*4, "Time");
        drawText(0, Core.helper.displayHeight()/16*4, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/16*5, "Criticality");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}