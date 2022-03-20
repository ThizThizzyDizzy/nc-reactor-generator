package net.ncplanner.plannerator.planner.s_tack.object;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
public class StackTuple extends StackCollection<List<StackObject>>{
    public StackTuple(Collection<StackObject> value){
        super(Arrays.asList(value.toArray(new StackObject[value.size()])));
    }
    @Override
    public String getTypeString(){
        return "tuple:"+size();
    }
    @Override
    public String toString(){
        String s = "tuple[";
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
        List<StackObject> copy = new ArrayList<>(value.size());
        for(StackObject o : value)copy.add(o.duplicate());
        return new StackTuple(copy); 
    }
    @Override
    public StackRange asRange(){
        return new StackRange(value);
    }
    @Override
    public StackList asList(){
        return new StackList(value);
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
        return obj.asTuple();
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
}