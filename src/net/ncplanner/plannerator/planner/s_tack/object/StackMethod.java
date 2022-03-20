package net.ncplanner.plannerator.planner.s_tack.object;
import net.ncplanner.plannerator.planner.s_tack.Script;
public class StackMethod extends StackObject{
    private final Script value;
    public StackMethod(Script value){
        this.value = value;
    }
    @Override
    public Type getType(){
        return Type.FUNCTION;
    }
    @Override
    public Script getValue(){
        return value;
    }
    @Override
    public String toString(){
        return "{...}";
    }
    @Override
    public StackObject duplicate(){
        return new StackMethod(value);
    }
    @Override
    public StackObject cast(StackObject obj){
        return obj.asMethod();
    }
}