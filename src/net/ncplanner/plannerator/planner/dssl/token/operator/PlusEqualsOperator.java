package net.ncplanner.plannerator.planner.dssl.token.operator;
public class PlusEqualsOperator extends AbstractEqualsOperator{
    public PlusEqualsOperator(){
        super("+=");
    }
    @Override
    public Operator newInstance(){
        return new PlusEqualsOperator();
    }
}