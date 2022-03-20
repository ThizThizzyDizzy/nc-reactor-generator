package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.object.StackFloat;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class ModuloOperator extends Operator{
    public ModuloOperator(){
        super("%%");
    }
    @Override
    public Operator newInstance(){
        return new ModuloOperator();
    }
    @Override
    public StackObject evaluate(StackObject v1, StackObject v2){
        if(v1.getBaseType()==StackObject.Type.INT&&v2.getBaseType()==StackObject.Type.INT)return new StackInt(((v1.asInt().getValue()%v2.asInt().getValue())+v2.asInt().getValue())%v2.asInt().getValue());
        return new StackFloat(((v1.asFloat().getValue()%v2.asFloat().getValue())+v2.asFloat().getValue())%v2.asFloat().getValue());
    }
}