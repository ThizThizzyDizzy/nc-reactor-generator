package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class XOrOperator extends Operator{
    public XOrOperator(){
        super("^");
    }
    @Override
    public Operator newInstance(){
        return new XOrOperator();
    }
    @Override
    public StackObject evaluate(StackObject v1, StackObject v2){
        return new StackBool(v1.asBool().getValue()^v2.asBool().getValue());
    }
}