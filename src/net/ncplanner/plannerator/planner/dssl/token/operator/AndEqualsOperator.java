package net.ncplanner.plannerator.planner.dssl.token.operator;
public class AndEqualsOperator extends AbstractEqualsOperator{
    public AndEqualsOperator(){
        super("&=");
    }
    @Override
    public Operator newInstance(){
        return new AndEqualsOperator();
    }
}