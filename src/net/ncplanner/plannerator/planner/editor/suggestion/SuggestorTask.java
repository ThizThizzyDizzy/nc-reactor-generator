package net.ncplanner.plannerator.planner.editor.suggestion;
import net.ncplanner.plannerator.planner.Task;
public class SuggestorTask extends Task{
    public int num = 0;
    public long time;
    public int max;
    public long maxTime;
    public SuggestorTask(Suggestor suggestor){
        super(suggestor.name);
        max = suggestor.limit;
        maxTime = suggestor.timeLimit;
    }
    @Override
    public double getProgressD(){
        return Math.max(num/(double)max, time/(double)maxTime);
    }
}