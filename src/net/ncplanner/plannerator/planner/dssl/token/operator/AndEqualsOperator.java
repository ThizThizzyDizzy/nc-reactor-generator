package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public class AndEqualsOperator extends AbstractEqualsOperator{
    public AndEqualsOperator(){
        super("&=");
    }
    @Override
    public Operator newInstance(){
        return new AndEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        if(var.getBaseType()==StackObject.Type.INT&&arg.getBaseType()==StackObject.Type.INT)return new StackInt(var.asInt().getValue()&arg.asInt().getValue());
        if(var.getBaseType()==StackObject.Type.BOOL&&arg.getBaseType()==StackObject.Type.BOOL)return new StackBool(var.asBool().getValue()&&arg.asBool().getValue());
        throw new IllegalArgumentException("Invalid operands for AndEquals! Expected two ints or two bools, found "+var.getBaseType().toString()+" and "+arg.getBaseType().toString());
    }
}