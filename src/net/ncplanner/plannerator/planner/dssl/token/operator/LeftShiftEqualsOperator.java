package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public class LeftShiftEqualsOperator extends AbstractEqualsOperator{
    public LeftShiftEqualsOperator(){
        super("<<=");
    }
    @Override
    public Operator newInstance(){
        return new LeftShiftEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        return new StackInt(var.asInt().getValue()<<arg.asInt().getValue());
    }
}