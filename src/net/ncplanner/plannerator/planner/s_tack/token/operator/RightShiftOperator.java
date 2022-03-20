package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class RightShiftOperator extends Operator{
    public RightShiftOperator(){
        super(">>");
    }
    @Override
    public Operator newInstance(){
        return new RightShiftOperator();
    }
    @Override
    public StackObject evaluate(Script script, StackObject v1, StackObject v2){
        return new StackInt(v1.asInt().getValue()>>v2.asInt().getValue());
    }
}