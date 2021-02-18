package planner.menu.configuration.overhaul.fissionsfr;
import multiblock.configuration.overhaul.fissionsfr.Source;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuSourceConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true)).setTooltip("The name of this neutron source. This should never change");
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Source source;
    public MenuSourceConfiguration(GUI gui, Menu parent, Source source){
        super(gui, parent);
        back.addActionListener((e) -> {
            source.name = name.text;
            source.efficiency = Float.parseFloat(efficiency.text);
            gui.open(parent);
        });
        this.source = source;
    }
    @Override
    public void onGUIOpened(){
        name.text = source.name;
        efficiency.text = source.efficiency+"";
    }
    @Override
    public void render(int millisSinceLastTick){
        back.width = name.width = gui.helper.displayWidth();
        efficiency.width = gui.helper.displayWidth()*.75;
        efficiency.x = gui.helper.displayWidth()*.25;
        efficiency.height = name.height = back.height = gui.helper.displayHeight()/16;
        efficiency.y = name.height;
        back.y = gui.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, gui.helper.displayHeight()/16, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/16*2, "Efficiency");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}