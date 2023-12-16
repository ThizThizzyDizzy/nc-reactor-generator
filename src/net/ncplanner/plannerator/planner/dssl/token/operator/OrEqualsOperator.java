package net.ncplanner.plannerator.planner.dssl.token.operator;
public class OrEqualsOperator extends AbstractEqualsOperator{
    public OrEqualsOperator(){
        super("|=");
    }
    @Override
    public Operator newInstance(){
        return new OrEqualsOperator();
    }
}