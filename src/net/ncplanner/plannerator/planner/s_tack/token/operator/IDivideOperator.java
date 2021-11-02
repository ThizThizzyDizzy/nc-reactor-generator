package net.ncplanner.plannerator.planner.s_tack.token.operator;
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
    public StackObject evaluate(StackObject v1, StackObject v2){
        return new StackInt((int)(v1.asNumber().getValue().floatValue()/v2.asNumber().getValue().floatValue()));
    }
}