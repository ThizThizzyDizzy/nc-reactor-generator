package net.ncplanner.plannerator.planner.ui.component;
import com.thizthizzydizzy.dizzyengine.graphics.Renderer;
import com.thizthizzydizzy.dizzyengine.ui.component.Component;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
public abstract class ProgressBar extends Component{
    private final float textHeight;
    private final float textInset;
    private final float progressBarHeight;
    public ProgressBar(){
        this(1);
    }
    public ProgressBar(float scale){
        this(20*scale, 2*scale, 6*scale);
    }
    public ProgressBar(float textHeight, float textInset, float progressBarHeight){
        this.textHeight = textHeight;
        this.textInset = textInset;
        this.progressBarHeight = progressBarHeight;
    }
    @Override
    public void draw(double deltaTime){
        Task task = getTask();
        float Y = y;
        while(task!=null){
            Renderer.setColor(Core.theme.getSecondaryComponentColor(Core.getThemeIndex(this)));
            Renderer.fillRect(x, Y, x+getWidth(), Y+textHeight+progressBarHeight+textInset*3);
            Renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            Renderer.drawText(x+textInset, Y+textInset, x+getWidth()-textInset, Y+textHeight+textInset, task.name);
            Renderer.setColor(Core.theme.getProgressBarBackgroundColor());
            Renderer.fillRect(x+textInset, Y+textHeight+textInset*2, x+getWidth()-textInset, Y+textHeight+progressBarHeight+textInset*2);
            float w = getWidth()-textInset*2;
            Renderer.setColor(Core.theme.getProgressBarColor());
            Renderer.fillRect(x+textInset, Y+textHeight+textInset*2, x+textInset+w*task.getProgressF(), Y+textHeight+progressBarHeight+textInset*2);
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