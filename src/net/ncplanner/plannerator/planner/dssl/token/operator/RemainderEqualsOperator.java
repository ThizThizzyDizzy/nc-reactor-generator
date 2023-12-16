package net.ncplanner.plannerator.planner.dssl.token.operator;
public class RemainderEqualsOperator extends AbstractEqualsOperator{
    public RemainderEqualsOperator(){
        super("%=");
    }
    @Override
    public Operator newInstance(){
        return new RemainderEqualsOperator();
    }
}