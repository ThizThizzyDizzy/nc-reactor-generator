package net.ncplanner.plannerator.planner.dssl.token.operator;
public class OrOperator extends Operator{
    public OrOperator(){
        super("|");
    }
    @Override
    public Operator newInstance(){
        return new OrOperator();
    }
}