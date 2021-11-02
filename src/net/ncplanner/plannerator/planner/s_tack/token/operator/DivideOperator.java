package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackFloat;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class DivideOperator extends Operator{
    public DivideOperator(){
        super("/");
    }
    @Override
    public Operator newInstance(){
        return new DivideOperator();
    }
    @Override
    public StackObject evaluate(StackObject v1, StackObject v2){
        boolean shouldInt = v1.getBaseType()==StackObject.Type.INT&&v2.getBaseType()==StackObject.Type.INT;
        float val = v1.asNumber().getValue().floatValue()/v2.asNumber().getValue().floatValue();
        if(shouldInt&&(int)val==val)return new StackInt((int)val);
        return new StackFloat(val);
    }
}