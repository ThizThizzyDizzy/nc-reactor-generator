package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackString;
public class ConcatOperator extends Operator{
    public ConcatOperator(){
        super("~");
    }
    @Override
    public Operator newInstance(){
        return new ConcatOperator();
    }
    @Override
    public StackObject evaluate(Script script, StackObject v1, StackObject v2){
        String s1 = "";
        switch(v1.getBaseType()){
            case BOOL:
            case INT:
            case FLOAT:
            case CHAR:
            case STRING:
                s1 = v1.getBaseValue().toString();
                break;
            default:
                throw new IllegalArgumentException("Cannot concat type: "+v1.getBaseType().toString());
        }
        String s2 = "";
        switch(v2.getBaseType()){
            case BOOL:
            case INT:
            case FLOAT:
            case CHAR:
            case STRING:
                s2 = v2.getBaseValue().toString();
                break;
            default:
                throw new IllegalArgumentException("Cannot concat type: "+v2.getBaseType().toString());
        }
        return new StackString(s1+s2);
    }
}