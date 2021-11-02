package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class LessThanOperator extends Operator{
    public LessThanOperator(){
        super("<");
    }
    @Override
    public Operator newInstance(){
        return new LessThanOperator();
    }
    @Override
    public StackObject evaluate(StackObject v1, StackObject v2){
        return new StackBool(v1.asNumber().getValue().floatValue()<v2.asNumber().getValue().floatValue());
    }
}