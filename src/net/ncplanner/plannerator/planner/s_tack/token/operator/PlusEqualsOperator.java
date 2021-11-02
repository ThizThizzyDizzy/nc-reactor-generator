package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackFloat;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
public class PlusEqualsOperator extends AbstractEqualsOperator{
    public PlusEqualsOperator(){
        super("+=");
    }
    @Override
    public Operator newInstance(){
        return new PlusEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        if(var.getBaseType()==StackObject.Type.INT&&arg.getBaseType()==StackObject.Type.INT){
            return new StackInt(var.asInt().getValue()+arg.asInt().getValue());
        }else{
            return new StackFloat(var.asNumber().getValue().floatValue()+arg.asNumber().getValue().floatValue());
        }
    }
}