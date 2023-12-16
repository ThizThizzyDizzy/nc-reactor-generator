package net.ncplanner.plannerator.planner.dssl.token.operator;
public class MultiplyEqualsOperator extends AbstractEqualsOperator{
    public MultiplyEqualsOperator(){
        super("*=");
    }
    @Override
    public Operator newInstance(){
        return new MultiplyEqualsOperator();
    }
}