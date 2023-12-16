package net.ncplanner.plannerator.planner.dssl.token.operator;
public class MinusOperator extends Operator{
    public MinusOperator(){
        super("-");
    }
    @Override
    public Operator newInstance(){
        return new MinusOperator();
    }
    @Override
    public String getOverload(){
        return "sub";
    }
}