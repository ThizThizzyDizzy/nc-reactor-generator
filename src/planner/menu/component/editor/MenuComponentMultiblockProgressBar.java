package planner.menu.component.editor;
import planner.Core;
import planner.Task;
import planner.menu.MenuEdit;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentMultiblockProgressBar extends MenuComponent{
    private static final double textHeight = 20;
    private static final double textInset = 2;
    private static final double progressBarHeight = 6;
    private final MenuEdit editor;
    public MenuComponentMultiblockProgressBar(MenuEdit editor, double x, double y, double width, double height){
        super(x, y, width, height);
        this.editor = editor;
    }
    @Override
    public void render(){
        Task task = editor.getTask();
        double Y = y;
        while(task!=null){
            Core.applyColor(Core.theme.getSecondaryComponentColor(Core.getThemeIndex(this)));
            drawRect(x, Y, x+width, Y+textHeight+progressBarHeight+textInset*3, 0);
            Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            drawText(x+textInset, Y+textInset, x+width-textInset, Y+textHeight+textInset, task.name);
            Core.applyColor(Core.theme.getProgressBarBackgroundColor());
            drawRect(x+textInset, Y+textHeight+textInset*2, x+width-textInset, Y+textHeight+progressBarHeight+textInset*2, 0);
            double w = width-textInset*2;
            Core.applyColor(Core.theme.getProgressBarColor());
            drawRect(x+textInset, Y+textHeight+textInset*2, x+textInset+w*task.getProgressD(), Y+textHeight+progressBarHeight+textInset*2, 0);
            task = task.getCurrentSubtask();
            Y+=textHeight+progressBarHeight+textInset*3;
        }
    }
}