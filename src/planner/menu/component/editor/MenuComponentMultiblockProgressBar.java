package planner.menu.component.editor;
import planner.Task;
import planner.menu.MenuEdit;
import planner.menu.component.MenuComponentProgressBar;
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