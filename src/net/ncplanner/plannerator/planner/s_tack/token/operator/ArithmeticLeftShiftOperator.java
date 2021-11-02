package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class ArithmeticLeftShiftOperator extends Operator{
    public ArithmeticLeftShiftOperator(){
        super("<<");
    }
    @Override
    public Operator newInstance(){
        return new ArithmeticLeftShiftOperator();
    }
    @Override
    public StackObject evaluate(StackObject v1, StackObject v2){
        return new StackInt(v1.asInt().getValue()<<v2.asInt().getValue());
    }
}