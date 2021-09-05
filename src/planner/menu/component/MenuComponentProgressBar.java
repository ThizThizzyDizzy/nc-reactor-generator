package planner.menu.component;
import planner.Core;
import planner.Task;
import simplelibrary.opengl.gui.components.MenuComponent;
public abstract class MenuComponentProgressBar extends MenuComponent{
    private final double textHeight;
    private final double textInset;
    private final double progressBarHeight;
    public MenuComponentProgressBar(double x, double y, double width, double height){
        this(x, y, width, height, 1);
    }
    public MenuComponentProgressBar(double x, double y, double width, double height, double scale){
        this(x, y, width, height, 20*scale, 2*scale, 6*scale);
    }
    public MenuComponentProgressBar(double x, double y, double width, double height, double textHeight, double textInset, double progressBarHeight){
        super(x, y, width, height);
        this.textHeight = textHeight;
        this.textInset = textInset;
        this.progressBarHeight = progressBarHeight;
    }
    @Override
    public void render(){
        Task task = getTask();
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
    public abstract Task getTask();
}