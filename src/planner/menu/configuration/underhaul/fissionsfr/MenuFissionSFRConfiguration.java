package planner.menu.configuration.underhaul.fissionsfr;
import org.lwjgl.opengl.Display;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuFissionSFRConfiguration extends Menu{
    private final MenuComponentMinimalistButton blocks = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Blocks", true, true));
    private final MenuComponentMinimalistButton fuels = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Fuels", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    public MenuFissionSFRConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        blocks.addActionListener((e) -> {
            gui.open(new MenuBlocksConfiguration(gui, this));
        });
        fuels.addActionListener((e) -> {
            gui.open(new MenuFuelsConfiguration(gui, this));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        blocks.label = "Blocks ("+Core.configuration.underhaul.fissionSFR.blocks.size()+")";
        fuels.label = "Fuels ("+Core.configuration.underhaul.fissionSFR.fuels.size()+")";
    }
    @Override
    public void render(int millisSinceLastTick){
        blocks.width = fuels.width = back.width = Display.getWidth();
        blocks.height = fuels.height = back.height = Display.getHeight()/16;
        fuels.y = blocks.height;
        back.y = Display.getHeight()-back.height;
        super.render(millisSinceLastTick);
    }
}