package planner.editor.suggestion;
import planner.Task;
public class SuggestorTask extends Task{
    private final Suggestor suggestor;
    public int num = 0;
    public long time;
    public SuggestorTask(Suggestor suggestor){
        super(suggestor.getName());
        this.suggestor = suggestor;
    }
    @Override
    public double getProgressD(){
        return Math.max(num/(double)suggestor.limit, time/(double)suggestor.timeLimit);
    }
}