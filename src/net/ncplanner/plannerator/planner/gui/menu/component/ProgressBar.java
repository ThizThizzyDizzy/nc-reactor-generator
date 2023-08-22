package net.ncplanner.plannerator.planner.gui.menu.component;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.gui.Component;
public abstract class ProgressBar extends Component{
    private final float textHeight;
    private final float textInset;
    private final float progressBarHeight;
    public ProgressBar(float scale){
        this(0, 0, 0, 0, scale);
    }
    public ProgressBar(float x, float y, float width, float height){
        this(x, y, width, height, 1);
    }
    public ProgressBar(float x, float y, float width, float height, float scale){
        this(x, y, width, height, 20*scale, 2*scale, 6*scale);
    }
    public ProgressBar(float x, float y, float width, float height, float textHeight, float textInset, float progressBarHeight){
        super(x, y, width, height);
        this.textHeight = textHeight;
        this.textInset = textInset;
        this.progressBarHeight = progressBarHeight;
    }
    @Override
    public void render2d(double deltaTime){
        Renderer renderer = new Renderer();
        Task task = getTask();
        float Y = y;
        while(task!=null){
            renderer.setColor(Core.theme.getSecondaryComponentColor(Core.getThemeIndex(this)));
            renderer.fillRect(x, Y, x+width, Y+textHeight+progressBarHeight+textInset*3);
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.drawText(x+textInset, Y+textInset, x+width-textInset, Y+textHeight+textInset, task.name);
            renderer.setColor(Core.theme.getProgressBarBackgroundColor());
            renderer.fillRect(x+textInset, Y+textHeight+textInset*2, x+width-textInset, Y+textHeight+progressBarHeight+textInset*2);
            float w = width-textInset*2;
            renderer.setColor(Core.theme.getProgressBarColor());
            renderer.fillRect(x+textInset, Y+textHeight+textInset*2, x+textInset+w*task.getProgressF(), Y+textHeight+progressBarHeight+textInset*2);
            task = task.getCurrentSubtask();
            Y+=textHeight+progressBarHeight+textInset*3;
        }
    }
    public abstract Task getTask();
    public float getTaskHeight(){
        float h = 0;
        Task task = getTask();
        while(task!=null){
            h+=textHeight+progressBarHeight+textInset*3;
            task = task.getCurrentSubtask();
        }
        return h;
    }
}