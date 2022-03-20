package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class OrOperator extends Operator{
    public OrOperator(){
        super("|");
    }
    @Override
    public Operator newInstance(){
        return new OrOperator();
    }
    @Override
    public StackObject evaluate(Script script, StackObject v1, StackObject v2){
        if(v1.getBaseType()==StackObject.Type.INT&&v2.getBaseType()==StackObject.Type.INT)return new StackInt(v1.asInt().getValue()|v2.asInt().getValue());
        if(v1.getBaseType()==StackObject.Type.BOOL&&v2.getBaseType()==StackObject.Type.BOOL)return new StackBool(v1.asBool().getValue()||v2.asBool().getValue());
        throw new IllegalArgumentException("Invalid operands for Or! Expected two ints or two bools, found "+v1.getBaseType().toString()+" and "+v2.getBaseType().toString());
    }
}