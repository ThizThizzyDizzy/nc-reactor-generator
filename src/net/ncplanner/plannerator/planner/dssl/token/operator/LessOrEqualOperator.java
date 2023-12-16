package net.ncplanner.plannerator.planner.dssl.token.operator;
public class LessOrEqualOperator extends Operator{
    public LessOrEqualOperator(){
        super("<=");
    }
    @Override
    public Operator newInstance(){
        return new LessOrEqualOperator();
    }
}