package net.ncplanner.plannerator.planner.dssl.token.operator;
public class PowerEqualsOperator extends AbstractEqualsOperator{
    public PowerEqualsOperator(){
        super("**=");
    }
    @Override
    public Operator newInstance(){
        return new PowerEqualsOperator();
    }
}