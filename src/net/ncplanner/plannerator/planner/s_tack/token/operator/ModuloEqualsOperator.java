package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackFloat;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
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