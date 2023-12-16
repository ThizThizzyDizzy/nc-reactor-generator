package net.ncplanner.plannerator.planner.dssl.token.operator;
public class PlusOperator extends Operator{
    public PlusOperator(){
        super("+");
    }
    @Override
    public Operator newInstance(){
        return new PlusOperator();
    }
    @Override
    public String getOverload(){
        return "add";
    }
}