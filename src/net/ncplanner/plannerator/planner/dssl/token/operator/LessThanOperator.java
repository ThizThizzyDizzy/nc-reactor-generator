package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class LessThanOperator extends Operator{
    public LessThanOperator(){
        super("<");
    }
    @Override
    public Operator newInstance(){
        return new LessThanOperator();
    }
    @Override
    public StackObject evaluate(Script script, StackObject v1, StackObject v2){
        return new StackBool(v1.asNumber().getValue().doubleValue()<v2.asNumber().getValue().doubleValue());
    }
}