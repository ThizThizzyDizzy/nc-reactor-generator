package net.ncplanner.plannerator.planner.dssl.token.operator;
public class PowerOperator extends Operator{
    public PowerOperator(){
        super("**");
    }
    @Override
    public Operator newInstance(){
        return new PowerOperator();
    }
    @Override
    public String getOverload(){
        return "pow";
    }
}