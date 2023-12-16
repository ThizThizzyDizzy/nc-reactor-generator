package net.ncplanner.plannerator.planner.dssl.token.operator;
public class RemainderOperator extends Operator{
    public RemainderOperator(){
        super("%");
    }
    @Override
    public Operator newInstance(){
        return new RemainderOperator();
    }
}