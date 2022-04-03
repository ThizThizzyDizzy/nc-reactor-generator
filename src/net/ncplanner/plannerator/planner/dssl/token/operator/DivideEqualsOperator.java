package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.object.StackFloat;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public class DivideEqualsOperator extends AbstractEqualsOperator{
    public DivideEqualsOperator(){
        super("/=");
    }
    @Override
    public Operator newInstance(){
        return new DivideEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        boolean shouldInt = var.getBaseType()==StackObject.Type.INT&&arg.getBaseType()==StackObject.Type.INT;
        double val = var.asNumber().getValue().doubleValue()/arg.asNumber().getValue().doubleValue();
        if(shouldInt&&(long)val==val)return new StackInt((long)val);
        return new StackFloat(val);
    }
}