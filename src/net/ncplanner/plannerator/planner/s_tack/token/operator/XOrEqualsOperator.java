package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
public class XOrEqualsOperator extends AbstractEqualsOperator{
    public XOrEqualsOperator(){
        super("^=");
    }
    @Override
    public Operator newInstance(){
        return new XOrEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        return new StackBool(var.asBool().getValue()^arg.asBool().getValue());
    }
}