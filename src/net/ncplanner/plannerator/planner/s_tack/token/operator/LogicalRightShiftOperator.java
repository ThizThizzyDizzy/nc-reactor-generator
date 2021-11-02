package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class LogicalRightShiftOperator extends Operator{
    public LogicalRightShiftOperator(){
        super(">>>");
    }
    @Override
    public Operator newInstance(){
        return new LogicalRightShiftOperator();
    }
    @Override
    public StackObject evaluate(StackObject v1, StackObject v2){
        return new StackInt(v1.asInt().getValue()>>>v2.asInt().getValue());
    }
}