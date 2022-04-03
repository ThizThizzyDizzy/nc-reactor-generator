package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public class RightShiftEqualsOperator extends AbstractEqualsOperator{
    public RightShiftEqualsOperator(){
        super(">>=");
    }
    @Override
    public Operator newInstance(){
        return new RightShiftEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        return new StackInt(var.asInt().getValue()>>arg.asInt().getValue());
    }
}