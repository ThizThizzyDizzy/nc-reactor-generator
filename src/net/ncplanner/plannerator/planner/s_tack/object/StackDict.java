package net.ncplanner.plannerator.planner.s_tack.object;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
public class StackDict extends StackCollection<HashMap<StackObject, StackObject>>{
    public StackDict(Collection<StackObject> value){
        super(parse(value));
    }
    public StackDict(Map<StackObject, StackObject> value){
        super(new HashMap<>(value));
    }
    private static HashMap<StackObject, StackObject> parse(Collection<StackObject> value){
        if(value.size()%2==1)throw new IllegalArgumentException("can't make odd dictionary!");
        HashMap<StackObject, StackObject> map = new HashMap<>();
        for(Iterator<StackObject> it = value.iterator(); it.hasNext();){
            map.put(it.next(), it.next());
        }
        return map;
    }
    @Override
    public String getTypeString(){
        return "dict";
    }
    @Override
    public String toString(){
        return "dict:"+value.size();
    }
    @Override
    public StackObject duplicate(){
        HashMap<StackObject, StackObject> copy = new HashMap<>();
        for(StackObject o : value.keySet())copy.put(o.duplicate(), value.get(o).duplicate());
        return new StackDict(copy); 
    }
    @Override
    public StackTuple asTuple(){
        return new StackTuple(toList());
    }
    @Override
    public StackSet asSet(){
        return new StackSet(toList());
    }
    @Override
    public StackList asList(){
        return new StackList(toList());
    }
    @Override
    public StackDict asDict(){
        return new StackDict(value);
    }
    public ArrayList<StackObject> toList(){
        ArrayList<StackObject> list = new ArrayList<>();
        for(Map.Entry<StackObject, StackObject> entry : value.entrySet()){
            list.add(entry.getKey());
            list.add(entry.getValue());
        }
        return list;
    }
    @Override
    public Collection<StackObject> collection(){
        return toList();
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
        return obj.asDict();
    }
    @Override
    public void clear(){
        value.clear();
    }
    @Override
    public StackObject get(StackObject elem){
        return value.get(elem);
    }
    @Override
    public void putAll(StackObject elem){
        for(Iterator<StackObject> it = elem.asCollection().collection().iterator(); it.hasNext();){
            value.put(it.next(), it.next());
        }
    }
    @Override
    public StackObject put(StackObject elem1, StackObject elem2){
        return value.put(elem1, elem2);
    }
    @Override
    public StackObject removeAll(Collection<StackObject> collection){
        HashSet<StackObject> set = new HashSet<>();
        for(StackObject o : collection){
            StackObject remove = value.remove(o);
            if(remove!=null)set.add(remove);
        }
        return new StackSet(set);
    }
    @Override
    public StackObject remove(StackObject elem){
        StackObject remove = value.remove(elem);
        return remove==null?StackNull.INSTANCE:remove;
    }
}