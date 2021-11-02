package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
public abstract class AbstractEqualsOperator extends Operator{
    public AbstractEqualsOperator(String operator){
        super(operator);
    }
    @Override
    public final StackObject evaluate(StackObject v1, StackObject v2){
        v1.asVariable().setValue(eval(v1.asVariable(),v2));
        return null;
    }
    public abstract StackObject eval(StackVariable var, StackObject arg);
}