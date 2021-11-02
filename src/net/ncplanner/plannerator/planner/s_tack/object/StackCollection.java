package net.ncplanner.plannerator.planner.s_tack.object;
import java.util.List;
public class StackCollection extends StackObject{
    private final StackObject[] value;
    public StackCollection(StackObject[] value){
        this.value = value;
    }
    public StackCollection(List<StackObject> value){
        this(value.toArray(new StackObject[value.size()]));
    }
    @Override
    public Type getType(){
        return Type.COLLECTION;
    }
    @Override
    public StackObject[] getValue(){
        return value;
    }
    @Override
    public String toString(){
        String s = "[";
        for(int i = 0; i<value.length; i++){
            StackObject obj = value[i];
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
        return new StackCollection(value.clone());
    }
}