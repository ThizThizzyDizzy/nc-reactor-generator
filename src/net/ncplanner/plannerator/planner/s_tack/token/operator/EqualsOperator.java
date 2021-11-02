package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
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