package planner.menu.configuration;
import org.lwjgl.opengl.Display;
import planner.menu.MenuTransition;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.configuration.underhaul.MenuUnderhaulConfiguration;
import planner.menu.configuration.overhaul.MenuOverhaulConfiguration;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuConfiguration extends Menu{
    private final MenuComponentMinimalistButton load = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Load Configuration", false, true));
    private final MenuComponentMinimalistButton save = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Save Configuration", false, true));
    private final MenuComponentMinimalistButton underhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Underhaul Configuration", true, true));
    private final MenuComponentMinimalistButton overhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Overhaul Configuration", true, true));
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true));
    public MenuConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        load.addActionListener((e) -> {
            throw new UnsupportedOperationException("Cannot load yet! :(");
            //TODO load from .ncpf (ncpc?) or NC config
        });
        save.addActionListener((e) -> {
            throw new UnsupportedOperationException("Cannot save yet! :(");
            //TODO save to .ncpf (ncpc?)
        });
        underhaul.addActionListener((e) -> {
            gui.open(new MenuUnderhaulConfiguration(gui, this));
        });
        overhaul.addActionListener((e) -> {
            gui.open(new MenuOverhaulConfiguration(gui, this));
        });
        done.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SlideTransition.slideTo(0, -1), 4));
        });
    }
    @Override
    public void render(int millisSinceLastTick){
        load.width = save.width = underhaul.width = overhaul.width = done.width = Display.getWidth();
        load.height = save.height = underhaul.height = overhaul.height = done.height = Display.getHeight()/16;
        save.y = load.y+load.height;
        underhaul.y = save.y+save.height;
        overhaul.y = underhaul.y+underhaul.height;
        done.y = Display.getHeight()-done.height;
        super.render(millisSinceLastTick);
    }
}