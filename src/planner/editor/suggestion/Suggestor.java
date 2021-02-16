package planner.editor.suggestion;
import java.util.ArrayList;
import multiblock.Multiblock;
public abstract class Suggestor<T extends Multiblock>{
    public final int limit;
    public final long timeLimit;
    public Suggestor(int limit, long timeLimit){
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
    public abstract String getName();
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
        public SuggestionAcceptor(T multiblock){
            this.multiblock = multiblock;
        }
        public void suggest(Suggestion<T> suggestion){
            if(!acceptingSuggestions()){
                denied(suggestion);
                return;
            }
            if(startTime==0)startTime = System.nanoTime();
            if(suggestion.test(multiblock)){
                accepted(suggestion);
                num++;
            }
            else denied(suggestion);
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