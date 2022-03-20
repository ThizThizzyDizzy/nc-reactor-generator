package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class IDivideOperator extends Operator{
    public IDivideOperator(){
        super("//");
    }
    @Override
    public Operator newInstance(){
        return new IDivideOperator();
    }
    @Override
    public StackObject evaluate(Script script, StackObject v1, StackObject v2){
        return new StackInt((long)(v1.asNumber().getValue().doubleValue()/v2.asNumber().getValue().doubleValue()));
    }
}