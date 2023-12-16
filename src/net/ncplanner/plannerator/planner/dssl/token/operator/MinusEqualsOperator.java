package net.ncplanner.plannerator.planner.dssl.token.operator;
public class MinusEqualsOperator extends AbstractEqualsOperator{
    public MinusEqualsOperator(){
        super("-=");
    }
    @Override
    public Operator newInstance(){
        return new MinusEqualsOperator();
    }
}