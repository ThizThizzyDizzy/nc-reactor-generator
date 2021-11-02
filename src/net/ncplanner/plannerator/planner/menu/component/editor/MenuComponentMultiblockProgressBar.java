package net.ncplanner.plannerator.planner.menu.component.editor;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.menu.MenuEdit;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentProgressBar;
public class MenuComponentMultiblockProgressBar extends MenuComponentProgressBar{
    private final MenuEdit editor;
    public MenuComponentMultiblockProgressBar(MenuEdit editor, double x, double y, double width, double height){
        super(x, y, width, height);
        this.editor = editor;
    }
    @Override
    public Task getTask(){
        return editor.getTask();
    }
}