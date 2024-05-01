package net.ncplanner.plannerator.planner.gui.menu.dialog;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.ProgressBar;
public class MenuTaskDialog extends MenuDialog{
    public Task task;
    public MenuTaskDialog(GUI gui, Menu parent, Task task){
        super(gui, parent);
        this.task = task;
        setContent(new ProgressBar(0, 0, 400, 0){
            @Override
            public Task getTask(){
                return task;
            }
        });
    }
    @Override
    public void open(){
        super.open();
        new Thread(() -> {
            runTask();
            close();
        }, "Task Dialog Thread: "+task.name).start();
    }
    public void runTask(){}
}