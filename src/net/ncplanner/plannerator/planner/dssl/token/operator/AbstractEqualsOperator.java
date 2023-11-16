package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public abstract class AbstractEqualsOperator extends Operator{
    public AbstractEqualsOperator(String operator){
        super(operator);
    }
    @Override
    public final StackObject evaluate(Script script, StackObject v1, StackObject v2){
        StackVariable var = v1.asLabel().getVariable();
        var.setValue(eval(var,v2).getBaseObject());
        return null;
    }
    public abstract StackObject eval(StackVariable var, StackObject arg);
}