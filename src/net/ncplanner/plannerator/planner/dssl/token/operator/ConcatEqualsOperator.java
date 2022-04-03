package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackString;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public class ConcatEqualsOperator extends AbstractEqualsOperator{
    public ConcatEqualsOperator(){
        super("~=");
    }
    @Override
    public Operator newInstance(){
        return new ConcatEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        String s1 = "";
        switch(var.getBaseType()){
            case BOOL:
            case INT:
            case FLOAT:
            case CHAR:
            case STRING:
                s1 = var.getBaseValue().toString();
                break;
            default:
                throw new IllegalArgumentException("Cannot concat type: "+var.getBaseType().toString());
        }
        String s2 = "";
        switch(arg.getBaseType()){
            case BOOL:
            case INT:
            case FLOAT:
            case CHAR:
            case STRING:
                s2 = arg.getBaseValue().toString();
                break;
            default:
                throw new IllegalArgumentException("Cannot concat type: "+arg.getBaseType().toString());
        }
        return new StackString(s1+s2);
    }
}