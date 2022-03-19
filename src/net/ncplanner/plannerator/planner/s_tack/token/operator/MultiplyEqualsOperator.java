package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackFloat;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
public class MultiplyEqualsOperator extends AbstractEqualsOperator{
    public MultiplyEqualsOperator(){
        super("*=");
    }
    @Override
    public Operator newInstance(){
        return new MultiplyEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        if(var.getBaseType()==StackObject.Type.INT&&arg.getBaseType()==StackObject.Type.INT){
            return new StackInt(var.asInt().getValue()*arg.asInt().getValue());
        }else{
            return new StackFloat(var.asNumber().getValue().doubleValue()*arg.asNumber().getValue().doubleValue());
        }
    }
}