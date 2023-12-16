package net.ncplanner.plannerator.planner.dssl.token.operator;
public class LessThanOperator extends Operator{
    public LessThanOperator(){
        super("<");
    }
    @Override
    public Operator newInstance(){
        return new LessThanOperator();
    }
}