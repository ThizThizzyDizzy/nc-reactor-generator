package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class LessOrEqualOperator extends Operator{
    public LessOrEqualOperator(){
        super("<=");
    }
    @Override
    public Operator newInstance(){
        return new LessOrEqualOperator();
    }
    @Override
    public StackObject evaluate(StackObject v1, StackObject v2){
        return new StackBool(v1.asNumber().getValue().doubleValue()<=v2.asNumber().getValue().doubleValue());
    }
    
}