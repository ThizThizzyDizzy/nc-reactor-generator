package planner.menu.configuration.overhaul.fissionmsr;
import planner.Core;
import multiblock.configuration.overhaul.fissionmsr.Source;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuSourceConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true));
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
        back.width = name.width = Core.helper.displayWidth();
        efficiency.width = Core.helper.displayWidth()*.75;
        efficiency.x = Core.helper.displayWidth()*.25;
        efficiency.height = name.height = back.height = Core.helper.displayHeight()/16;
        efficiency.y = name.height;
        back.y = Core.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, Core.helper.displayHeight()/16, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/16*2, "Efficiency");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}