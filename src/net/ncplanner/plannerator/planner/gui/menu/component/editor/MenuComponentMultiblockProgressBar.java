package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.gui.menu.MenuEdit;
import net.ncplanner.plannerator.planner.gui.menu.component.ProgressBar;
public class MenuComponentMultiblockProgressBar extends ProgressBar{
    private final MenuEdit editor;
    public MenuComponentMultiblockProgressBar(MenuEdit editor, float x, float y, float width, float height){
        super(x, y, width, height);
        this.editor = editor;
    }
    @Override
    public Task getTask(){
        return editor.getTask();
    }
}