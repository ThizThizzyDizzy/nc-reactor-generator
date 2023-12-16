package net.ncplanner.plannerator.planner.dssl.token.operator;
public class ConcatEqualsOperator extends AbstractEqualsOperator{
    public ConcatEqualsOperator(){
        super("~=");
    }
    @Override
    public Operator newInstance(){
        return new ConcatEqualsOperator();
    }
}