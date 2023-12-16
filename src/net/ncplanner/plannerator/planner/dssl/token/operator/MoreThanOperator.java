package net.ncplanner.plannerator.planner.dssl.token.operator;
public class MoreThanOperator extends Operator{
    public MoreThanOperator(){
        super(">");
    }
    @Override
    public Operator newInstance(){
        return new MoreThanOperator();
    }
}