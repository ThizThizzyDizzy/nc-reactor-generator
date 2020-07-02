package planner.menu.configuration.overhaul;
import planner.menu.configuration.overhaul.fissionsfr.MenuFissionSFRConfiguration;
import planner.menu.configuration.overhaul.fissionmsr.MenuFissionMSRConfiguration;
import org.lwjgl.opengl.Display;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuOverhaulConfiguration extends Menu{
    private final MenuComponentMinimalistButton fissionSFR = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Solid-Fueled Fission Configuration", Core.configuration.overhaul.fissionSFR!=null, true));
    private final MenuComponentMinimalistButton fissionMSR = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Molten Salt Fission Configuration", Core.configuration.overhaul.fissionMSR!=null, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    public MenuOverhaulConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        fissionSFR.addActionListener((e) -> {
            gui.open(new MenuFissionSFRConfiguration(gui, this));
        });
        fissionMSR.addActionListener((e) -> {
            gui.open(new MenuFissionMSRConfiguration(gui, this));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void render(int millisSinceLastTick){
        fissionSFR.width = fissionMSR.width = back.width = Display.getWidth();
        fissionSFR.height = fissionMSR.height = back.height = Display.getHeight()/16;
        fissionMSR.y = fissionSFR.height;
        back.y = Display.getHeight()-back.height;
        super.render(millisSinceLastTick);
    }
}