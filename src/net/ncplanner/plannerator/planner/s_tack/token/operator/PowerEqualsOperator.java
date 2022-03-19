package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackFloat;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
public class PowerEqualsOperator extends AbstractEqualsOperator{
    public PowerEqualsOperator(){
        super("**=");
    }
    @Override
    public Operator newInstance(){
        return new PowerEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        if(var.getBaseType()==StackObject.Type.INT&&arg.getBaseType()==StackObject.Type.INT){
            return new StackInt((long)Math.pow(var.asInt().getValue(), arg.asInt().getValue()));
        }else{
            return new StackFloat(Math.pow(var.asNumber().getValue().doubleValue(), arg.asNumber().getValue().doubleValue()));
        }
    }
}