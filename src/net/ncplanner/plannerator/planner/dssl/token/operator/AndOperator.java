package net.ncplanner.plannerator.planner.dssl.token.operator;
public class AndOperator extends Operator{
    public AndOperator(){
        super("&");
    }
    @Override
    public Operator newInstance(){
        return new AndOperator();
    }
}