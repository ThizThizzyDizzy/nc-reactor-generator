package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
public class LogicalRightShiftEqualsOperator extends AbstractEqualsOperator{
    public LogicalRightShiftEqualsOperator(){
        super(">>>=");
    }
    @Override
    public Operator newInstance(){
        return new LogicalRightShiftEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        return new StackInt(var.asInt().getValue()>>>arg.asInt().getValue());
    }
}