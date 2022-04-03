package net.ncplanner.plannerator.planner.dssl.object;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
public class StackList extends StackCollection<ArrayList<StackObject>>{
    public StackList(Collection<StackObject> value){
        super(new ArrayList<>(value));
    }
    @Override
    public String getTypeString(){
        return "list";
    }
    @Override
    public String toString(){
        String s = "list[";
        for(int i = 0; i<value.size(); i++){
            StackObject obj = value.get(i);
            s+=" "+obj.toString();
            if(i==10){
                s+=" ...";
                break;
            }
        }
        return s+" ]";
    }
    @Override
    public StackObject duplicate(){
        ArrayList<StackObject> copy = new ArrayList<>(value.size());
        for(StackObject o : value)copy.add(o.duplicate());
        return new StackList(copy); 
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
        return obj.asList();
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
    public StackObject get(StackObject elem){
        return value.get((int)(long)elem.asInt().getValue());
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
    public void putAll(StackObject elem){
        for(Iterator<StackObject> it = elem.asCollection().collection().iterator(); it.hasNext();){
            value.set((int)(long)it.next().asInt().getValue(), it.next());
        }
    }
    @Override
    public StackObject put(StackObject elem1, StackObject elem2){
        return value.set((int)(long)elem1.asInt().getValue(), elem2);
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