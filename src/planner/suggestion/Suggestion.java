package planner.suggestion;
import generator.Priority;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Multiblock;
public class Suggestion<T extends Multiblock> implements Comparable<Suggestion<T>>{
    private final ArrayList<Action<T>> suggestedActions;
    private final ArrayList<Priority<T>> priorities;
    public Multiblock result;
    public Suggestion(Action<T> suggestedAction, ArrayList<Priority<T>> priorities){
        this(new ArrayList<>(), priorities);
        suggestedActions.add(suggestedAction);
    }
    public Suggestion(ArrayList<Action<T>> suggestedActions, ArrayList<Priority<T>> priorities){
        this.suggestedActions = suggestedActions;
        this.priorities = priorities;
    }
    public boolean test(T multiblock){
        result = multiblock.copy();
        for(Action<T> a : suggestedActions)result.action(a, true);
        return result.isBetterThan(multiblock, priorities);
    }
    @Override
    public int compareTo(Suggestion<T> other){
        return result.compareTo(other.result, priorities);
    }
}