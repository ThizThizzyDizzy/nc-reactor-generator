package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
public abstract class AbstractEqualsOperator extends Operator{
    public AbstractEqualsOperator(String operator){
        super(operator);
    }
    @Override
    public final StackObject evaluate(Script script, StackObject v1, StackObject v2){
        StackVariable var = script.variables.get(v1.asLabel().getValue());
        var.setValue(eval(var,v2));
        return null;
    }
    public abstract StackObject eval(StackVariable var, StackObject arg);
}