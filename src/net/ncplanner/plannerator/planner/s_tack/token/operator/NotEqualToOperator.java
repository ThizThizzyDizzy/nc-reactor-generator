package net.ncplanner.plannerator.planner.s_tack.token.operator;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class NotEqualToOperator extends Operator{
    public NotEqualToOperator(){
        super("!=");
    }
    @Override
    public Operator newInstance(){
        return new NotEqualToOperator();
    }
    @Override
    public StackObject evaluate(StackObject v1, StackObject v2){
        return new StackBool(!v1.getBaseValue().equals(v2.getBaseValue()));
    }
}