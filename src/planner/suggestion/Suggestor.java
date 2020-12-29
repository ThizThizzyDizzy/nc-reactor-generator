package planner.suggestion;
import java.util.ArrayList;
import java.util.Collections;
import multiblock.Multiblock;
public abstract class Suggestor<T extends Multiblock>{
    private final int limit;
    private final long timeLimit;
    public Suggestor(int limit, long timeLimit){
        this.limit = limit;
        this.timeLimit = timeLimit;
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
    public final void generateSuggestions(T multiblock, ArrayList<Suggestion<T>> suggestions){
        generateSuggestions(multiblock, new SuggestionAcceptor(multiblock) {
            @Override
            protected void accepted(Suggestion<T> suggestion){
                suggestions.add(suggestion);
            }
            @Override
            protected void denied(Suggestion<T> suggestion){}
        });
        Collections.sort(suggestions);
    }
    public abstract void generateSuggestions(T multiblock, SuggestionAcceptor suggestor);
    protected abstract class SuggestionAcceptor{
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
            if(startTime!=0&&System.nanoTime()>startTime+timeLimit*1_000_000)return false;
            return num<limit;
        }
    }
}