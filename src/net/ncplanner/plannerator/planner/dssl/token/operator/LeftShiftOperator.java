package net.ncplanner.plannerator.planner.dssl.token.operator;
public class LeftShiftOperator extends Operator{
    public LeftShiftOperator(){
        super("<<");
    }
    @Override
    public Operator newInstance(){
        return new LeftShiftOperator();
    }
}