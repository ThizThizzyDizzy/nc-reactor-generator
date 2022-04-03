package net.ncplanner.plannerator.planner.dssl.object;
import java.util.Collection;
import java.util.HashSet;
public class StackSet extends StackCollection<HashSet<StackObject>>{
    public StackSet(Collection<StackObject> value){
        super(new HashSet<>(value));
    }
    @Override
    public String getTypeString(){
        return "set";
    }
    @Override
    public String toString(){
        return "set:"+value.size();
    }
    @Override
    public StackObject duplicate(){
        HashSet<StackObject> copy = new HashSet<>(value.size());
        for(StackObject o : value)copy.add(o.duplicate());
        return new StackSet(copy); 
    }
    @Override
    public StackRange asRange(){
        return new StackRange(value);
    }
    @Override
    public StackTuple asTuple(){
        return new StackTuple(value);
    }
    @Override
    public StackSet asSet(){
        return new StackSet(value);
    }
    @Override
    public StackDict asDict(){
        if(value.size()%2==0)return new StackDict(value);
        return super.asDict();
    }
    @Override
    public Collection<StackObject> collection(){
        return value;
    }
    @Override
    public long size(){
        return value.size();
    }
    @Override
    public boolean isEmpty(){
        return value.isEmpty();
    }
    @Override
    public StackObject cast(StackObject obj){
        return obj.asSet();
    }
    @Override
    public boolean addAll(Collection<StackObject> collection){
        return value.addAll(collection);
    }
    @Override
    public boolean add(StackObject elem){
        return value.add(elem);
    }
    @Override
    public void clear(){
        value.clear();
    }
    @Override
    public boolean containsAll(Collection<StackObject> collection){
        return value.containsAll(collection);
    }
    @Override
    public boolean contains(StackObject elem){
        return value.contains(elem);
    }
    @Override
    public StackObject removeAll(Collection<StackObject> collection){
        return new StackBool(value.removeAll(collection));
    }
    @Override
    public StackObject remove(StackObject elem){
        return new StackBool(value.remove(elem));
    }
}