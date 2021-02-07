package planner.menu;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuControls extends Menu{
    private final MenuComponentMinimalistOptionButton invertUndoRedo = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Undo/Redo", true, true, Core.invertUndoRedo?1:0, "Standard", "Inverted"));
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true).setTooltip("Close the settings menu"));
    public MenuControls(GUI gui, Menu parent){
        super(gui, parent);
        done.addActionListener((e) -> {
            Core.invertUndoRedo = invertUndoRedo.getIndex()==1;
            gui.open(parent);
        });
    }
    @Override
    public void render(int millisSinceLastTick){
        done.width = invertUndoRedo.width = gui.helper.displayWidth();
        done.height = invertUndoRedo.height = gui.helper.displayHeight()/16;
        done.y = gui.helper.displayHeight()-done.height;
        super.render(millisSinceLastTick);
    }
}