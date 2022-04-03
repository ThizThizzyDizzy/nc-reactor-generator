package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class MoreThanOperator extends Operator{
    public MoreThanOperator(){
        super(">");
    }
    @Override
    public Operator newInstance(){
        return new MoreThanOperator();
    }
    @Override
    public StackObject evaluate(Script script, StackObject v1, StackObject v2){
        return new StackBool(v1.asNumber().getValue().doubleValue()>v2.asNumber().getValue().doubleValue());
    }
}