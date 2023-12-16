package net.ncplanner.plannerator.planner.dssl.token.operator;
public class EqualsOperator extends AbstractEqualsOperator{
    public EqualsOperator(){
        super("=");
    }
    @Override
    public Operator newInstance(){
        return new EqualsOperator();
    }
}