package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackFloat;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
public class DivideEqualsOperator extends AbstractEqualsOperator{
    public DivideEqualsOperator(){
        super("/=");
    }
    @Override
    public Operator newInstance(){
        return new DivideEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        boolean shouldInt = var.getBaseType()==StackObject.Type.INT&&arg.getBaseType()==StackObject.Type.INT;
        float val = var.asNumber().getValue().floatValue()/arg.asNumber().getValue().floatValue();
        if(shouldInt&&(int)val==val)return new StackInt((int)val);
        return new StackFloat(val);
    }
}