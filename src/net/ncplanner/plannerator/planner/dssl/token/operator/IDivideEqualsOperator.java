package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public class IDivideEqualsOperator extends AbstractEqualsOperator{
    public IDivideEqualsOperator(){
        super("//=");
    }
    @Override
    public Operator newInstance(){
        return new IDivideEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        return new StackInt((long)(var.asNumber().getValue().doubleValue()/arg.asNumber().getValue().doubleValue()));
    }
}