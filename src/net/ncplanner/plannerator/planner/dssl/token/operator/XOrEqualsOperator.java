package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
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