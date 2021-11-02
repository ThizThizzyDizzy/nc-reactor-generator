package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
public class OrEqualsOperator extends AbstractEqualsOperator{
    public OrEqualsOperator(){
        super("|=");
    }
    @Override
    public Operator newInstance(){
        return new OrEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        if(var.getBaseType()==StackObject.Type.INT&&arg.getBaseType()==StackObject.Type.INT)return new StackInt(var.asInt().getValue()|arg.asInt().getValue());
        if(var.getBaseType()==StackObject.Type.BOOL&&arg.getBaseType()==StackObject.Type.BOOL)return new StackBool(var.asBool().getValue()||arg.asBool().getValue());
        throw new IllegalArgumentException("Invalid operands for OrEquals! Expected two ints or two bools, found "+var.getBaseType().toString()+" and "+arg.getBaseType().toString());
    }
}