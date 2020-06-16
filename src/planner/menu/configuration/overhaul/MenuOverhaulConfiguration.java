package planner.menu.configuration.overhaul;
import planner.menu.configuration.overhaul.fissionsfr.MenuFissionSFRConfiguration;
import org.lwjgl.opengl.Display;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuOverhaulConfiguration extends Menu{
    private final MenuComponentMinimalistButton fissionSFR = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Solid-Fueled Fission Configuration", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    public MenuOverhaulConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        fissionSFR.addActionListener((e) -> {
            gui.open(new MenuFissionSFRConfiguration(gui, this));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void render(int millisSinceLastTick){
        fissionSFR.width = back.width = Display.getWidth();
        fissionSFR.height = back.height = Display.getHeight()/16;
        back.y = Display.getHeight()-back.height;
        super.render(millisSinceLastTick);
    }
}