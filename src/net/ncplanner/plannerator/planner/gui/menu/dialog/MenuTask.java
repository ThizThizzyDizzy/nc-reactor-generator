package net.ncplanner.plannerator.planner.gui.menu.dialog;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.ui.component.ProgressBar;
public class MenuTask extends MenuDialog{
    private final Task task;
    public MenuTask(GUI gui, Menu parent, Task task){
        throw new UnsupportedOperationException("Pending refactor");
//
//         super(gui, parent);
//         throw new UnsupportedOperationException("Pending refactor");
// //
// //
// //         setContent(new ProgressBar(0, 0, 400, 0){
// //             @Override
// //             public Task getTask(){
// //                 return task;
// //             }
// //         });
// //         this.task = task;
// //
//
    }
    /* @Override */
    public void render2d(double deltaTime){
        throw new UnsupportedOperationException("Pending refactor");
//
//         super.render2d(deltaTime);
//         if(task.isFinished()&&gui.menu==this)close();
//
    }
}