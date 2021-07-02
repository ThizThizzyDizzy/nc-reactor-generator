package planner;
import java.util.ArrayList;
public class Task{
    public String name;
    private ArrayList<Task> subtasks = new ArrayList<>();
    public double progress = 0;
    private boolean finished = false;
    public Task(String name){
        this.name = name;
    }
    public synchronized <T extends Task> T addSubtask(T subtask){
        subtasks.add(subtask);
        return subtask;
    }
    public synchronized Task addSubtask(String name){
        return addSubtask(new Task(name));
    }
    public synchronized Task getCurrentSubtask(){
        for(Task t : subtasks){
            if(t.isFinished())continue;
            return t;
        }
        return null;
    }
    public void finish(){
        finished = true;
    }
    public synchronized boolean isFinished(){
        if(subtasks.isEmpty()){
            return finished;
        }else{
            for(Task task : subtasks){
                if(!task.isFinished())return false;
            }
            return true;
        }
    }
    private synchronized double getProgress(){
        if(subtasks.isEmpty())return 0;
        double prog = 0;
        for(Task task : subtasks){
            prog+=task.isFinished()?1:task.getProgressD();
        }
        return prog;
    }
    public synchronized double getProgressD(){
        if(subtasks.isEmpty())return finished?1:progress;
        return getProgress()/subtasks.size();
    }
}