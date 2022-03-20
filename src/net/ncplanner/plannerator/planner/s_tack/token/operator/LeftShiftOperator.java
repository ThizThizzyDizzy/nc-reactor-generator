package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class LeftShiftOperator extends Operator{
    public LeftShiftOperator(){
        super("<<");
    }
    @Override
    public Operator newInstance(){
        return new LeftShiftOperator();
    }
    @Override
    public StackObject evaluate(Script script, StackObject v1, StackObject v2){
        return new StackInt(v1.asInt().getValue()<<v2.asInt().getValue());
    }
}