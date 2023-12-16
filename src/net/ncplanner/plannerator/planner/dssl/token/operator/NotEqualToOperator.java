package net.ncplanner.plannerator.planner.dssl.token.operator;
public class NotEqualToOperator extends Operator{
    public NotEqualToOperator(){
        super("!=");
    }
    @Override
    public Operator newInstance(){
        return new NotEqualToOperator();
    }
    @Override
    public String getOverload(){
        return "ne";
    }
}