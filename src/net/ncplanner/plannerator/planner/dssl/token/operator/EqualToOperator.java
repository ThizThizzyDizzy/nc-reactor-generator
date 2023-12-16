package net.ncplanner.plannerator.planner.dssl.token.operator;
public class EqualToOperator extends Operator{
    public EqualToOperator(){
        super("==");
    }
    @Override
    public Operator newInstance(){
        return new EqualToOperator();
    }
    @Override
    public String getOverload(){
        return "eq";
    }
}
