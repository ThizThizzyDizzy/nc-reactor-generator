package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
import net.ncplanner.plannerator.planner.dssl.object.StackNull;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class EqualToOperator extends Operator{
    public EqualToOperator(){
        super("==");
    }
    @Override
    public Operator newInstance(){
        return new EqualToOperator();
    }
    @Override
    public StackObject evaluate(Script script, StackObject v1, StackObject v2){
        if(v1==StackNull.INSTANCE||v2==StackNull.INSTANCE)return new StackBool(v2==v1);
        return new StackBool(v1.getBaseValue().equals(v2.getBaseValue()));
    }
}