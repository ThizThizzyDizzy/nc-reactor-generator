package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.object.StackFloat;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public class ModuloEqualsOperator extends AbstractEqualsOperator{
    public ModuloEqualsOperator(){
        super("%%=");
    }
    @Override
    public Operator newInstance(){
        return new ModuloEqualsOperator();
    }
    @Override
    public StackObject eval(StackVariable var, StackObject arg){
        if(var.getBaseType()==StackObject.Type.INT&&arg.getBaseType()==StackObject.Type.INT)return new StackInt(((var.asInt().getValue()%arg.asInt().getValue())+arg.asInt().getValue())%arg.asInt().getValue());
        return new StackFloat(((var.asFloat().getValue()%arg.asFloat().getValue())+arg.asFloat().getValue())%arg.asFloat().getValue());
    }
}