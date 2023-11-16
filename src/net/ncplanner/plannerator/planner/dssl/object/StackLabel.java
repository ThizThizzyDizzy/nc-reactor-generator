package net.ncplanner.plannerator.planner.dssl.object;
import net.ncplanner.plannerator.planner.dssl.Script;
public class StackLabel extends StackObject{
    private final String value;
    public final Script scope;
    public StackLabel(String value, Script scope){
        this.value = value;
        this.scope = scope;
    }
    @Override
    public Type getType(){
        return Type.LABEL;
    }
    @Override
    public String getValue(){
        return value;
    }
    public StackVariable getVariable(){
        return scope.variables.get(getValue());
    }
    @Override
    public String toString(){
        return "LABEL{"+value+"}";
    }
    @Override
    public StackObject duplicate(){
        return new StackLabel(value, scope);
    }
    @Override
    public StackObject cast(StackObject obj){
        return obj.asLabel();
    }
}