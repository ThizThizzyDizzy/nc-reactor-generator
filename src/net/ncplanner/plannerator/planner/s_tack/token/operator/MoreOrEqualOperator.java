package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class MoreOrEqualOperator extends Operator{
    public MoreOrEqualOperator(){
        super(">=");
    }
    @Override
    public Operator newInstance(){
        return new MoreOrEqualOperator();
    }
    @Override
    public StackObject evaluate(StackObject v1, StackObject v2){
        return new StackBool(v1.asNumber().getValue().floatValue()>=v2.asNumber().getValue().floatValue());
    }
}