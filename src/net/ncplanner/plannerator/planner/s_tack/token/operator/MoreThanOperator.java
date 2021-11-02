package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class MoreThanOperator extends Operator{
    public MoreThanOperator(){
        super(">");
    }
    @Override
    public Operator newInstance(){
        return new MoreThanOperator();
    }
    @Override
    public StackObject evaluate(StackObject v1, StackObject v2){
        return new StackBool(v1.asNumber().getValue().floatValue()>v2.asNumber().getValue().floatValue());
    }
}