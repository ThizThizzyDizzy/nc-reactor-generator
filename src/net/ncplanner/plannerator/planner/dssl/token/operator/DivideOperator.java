package net.ncplanner.plannerator.planner.dssl.token.operator;
public class DivideOperator extends Operator{
    public DivideOperator(){
        super("/");
    }
    @Override
    public Operator newInstance(){
        return new DivideOperator();
    }
    @Override
    public String getOverload(){
        return "div";
    }
}