package net.ncplanner.plannerator.planner.dssl.token.operator;
public class MoreOrEqualOperator extends Operator{
    public MoreOrEqualOperator(){
        super(">=");
    }
    @Override
    public Operator newInstance(){
        return new MoreOrEqualOperator();
    }
}