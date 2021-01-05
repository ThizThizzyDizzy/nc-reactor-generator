package planner.editor.suggestion;
import generator.Priority;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
public class Suggestion<T extends Multiblock> implements Comparable<Suggestion<T>>{
    private final ArrayList<Action<T>> suggestedActions;
    private final ArrayList<Priority<T>> priorities;
    public T result;
    private ArrayList<Block> affectedBlocks = new ArrayList<>();
    private static <T extends Multiblock> ArrayList<Action<T>> box(Action<T> action){
        ArrayList<Action<T>> list = new ArrayList<>();
        list.add(action);
        return list;
    }
    public boolean selected = false;
    private final String name;
    public Suggestion(String name, Action<T> suggestedAction, ArrayList<Priority<T>> priorities){
        this(name, box(suggestedAction), priorities);
    }
    public Suggestion(String name, ArrayList<Action<T>> suggestedActions, ArrayList<Priority<T>> priorities){
        this.suggestedActions = suggestedActions;
        this.priorities = priorities;
        this.name = name;
    }
    public boolean test(T multiblock){
        result = (T)multiblock.copy();
        for(Action<T> a : suggestedActions){
            a.getAffectedBlocks(result, affectedBlocks);
            result.action(a, true);
            a.getAffectedBlocks(result, affectedBlocks);
        }
        return result.isBetterThan(multiblock, priorities);
    }
    @Override
    public int compareTo(Suggestion<T> other){
        return other.result.compareTo(result, priorities);
    }
    public boolean affects(int x, int y, int z){
        for(Block b : affectedBlocks){
            if(b.x==x&&b.y==y&&b.z==z)return true;
        }
        return false;
    }
    public String getDescription(){
        //TODO literally make a multiblock tooltip diff
        return null;
    }
    public String getName(){
        return name;
    }
    public void apply(Multiblock multiblock){
        for(Action<T> a : suggestedActions){
            multiblock.action(a, true);
        }
    }
}