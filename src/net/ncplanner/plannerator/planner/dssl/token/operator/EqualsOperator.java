package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public class EqualsOperator extends AbstractEqualsOperator{
    public EqualsOperator(){
        super("=");
    }
    @Override
    public Operator newInstance(){
        return new EqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        return arg;
    }
}