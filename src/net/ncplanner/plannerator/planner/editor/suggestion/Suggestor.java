package net.ncplanner.plannerator.planner.editor.suggestion;
import java.util.function.Consumer;
import net.ncplanner.plannerator.multiblock.Multiblock;
public abstract class Suggestor<T extends Multiblock>{
    public final String name;
    public final int limit;
    public final long timeLimit;
    public int pruneAt = 1024;
    public int pruneTo = 512;
    public Suggestor(String name, int limit, long timeLimit){
        this.name = name;
        this.limit = limit==-1?Integer.MAX_VALUE:limit;
        this.timeLimit = timeLimit==-1?Long.MAX_VALUE:timeLimit;
    }
    private boolean active = false;
    public void activate(){
        active = true;
        onActivated();
    }
    public void deactivate(){
        active = false;
        onDeactivated();
    }
    public boolean isActive(){
        return active;
    }
    protected void onActivated(){}
    protected void onDeactivated(){}
    public abstract String getDescription();
    public void setActive(boolean active){
        if(active)activate();
        else deactivate();
    }
    public abstract void generateSuggestions(T multiblock, SuggestionAcceptor suggestor);
    public abstract class SuggestionAcceptor{
        private int num = 0;
        private final T multiblock;
        private long startTime;
        public final SuggestorTask task;
        private boolean countSet = false;
        public SuggestionAcceptor(T multiblock, SuggestorTask task){
            this.multiblock = multiblock;
            this.task = task;
        }
        public void suggest(Suggestion<T> suggestion){
            suggest(suggestion, null);
        }
        public void suggest(Suggestion<T> suggestion, Consumer<SuggestorTask> updateTask){
            if(!acceptingSuggestions()){
                denied(suggestion);
                return;
            }
            if(startTime==0)startTime = System.nanoTime();
            if(suggestion.test(multiblock)){
                accepted(suggestion);
                num++;
                task.num++;
            }else{
                denied(suggestion);
                if(countSet)task.num++;
            }
            if(updateTask!=null)updateTask.accept(task);
            task.time = elapsedTime();
        }
        public void setCount(int count){
            countSet = true;
            task.max = count;
        }
        protected abstract void accepted(Suggestion<T> suggestion);
        protected abstract void denied(Suggestion<T> suggestion);
        public boolean acceptingSuggestions(){
            if(startTime!=0&&elapsedTime()>timeLimit)return false;
            return num<limit;
        }
        public long elapsedTime(){
            return (System.nanoTime()-startTime)/1_000_000;
        }
    }
}