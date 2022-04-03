package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.object.StackFloat;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public class PlusEqualsOperator extends AbstractEqualsOperator{
    public PlusEqualsOperator(){
        super("+=");
    }
    @Override
    public Operator newInstance(){
        return new PlusEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        if(var.getBaseType()==StackObject.Type.INT&&arg.getBaseType()==StackObject.Type.INT){
            return new StackInt(var.asInt().getValue()+arg.asInt().getValue());
        }else{
            return new StackFloat(var.asNumber().getValue().doubleValue()+arg.asNumber().getValue().doubleValue());
        }
    }
}