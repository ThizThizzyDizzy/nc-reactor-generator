package net.ncplanner.plannerator.planner.dssl.token.operator;
public class MultiplyOperator extends Operator{
    public MultiplyOperator(){
        super("*");
    }
    @Override
    public Operator newInstance(){
        return new MultiplyOperator();
    }
    @Override
    public String getOverload(){
        return "mul";
    }
}