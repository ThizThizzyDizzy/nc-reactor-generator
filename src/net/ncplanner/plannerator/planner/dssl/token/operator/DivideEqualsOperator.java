package net.ncplanner.plannerator.planner.dssl.token.operator;
public class DivideEqualsOperator extends AbstractEqualsOperator{
    public DivideEqualsOperator(){
        super("/=");
    }
    @Override
    public Operator newInstance(){
        return new DivideEqualsOperator();
    }
}