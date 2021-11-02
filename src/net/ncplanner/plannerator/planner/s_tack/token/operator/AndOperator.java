package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class AndOperator extends Operator{
    public AndOperator(){
        super("&");
    }
    @Override
    public Operator newInstance(){
        return new AndOperator();
    }
    @Override
    public StackObject evaluate(StackObject v1, StackObject v2){
        if(v1.getBaseType()==StackObject.Type.INT&&v2.getBaseType()==StackObject.Type.INT)return new StackInt(v1.asInt().getValue()&v2.asInt().getValue());
        if(v1.getBaseType()==StackObject.Type.BOOL&&v2.getBaseType()==StackObject.Type.BOOL)return new StackBool(v1.asBool().getValue()&&v2.asBool().getValue());
        throw new IllegalArgumentException("Invalid operands for And! Expected two ints or two bools, found "+v1.getBaseType().toString()+" and "+v2.getBaseType().toString());
    }
}