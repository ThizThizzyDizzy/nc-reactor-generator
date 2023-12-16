package net.ncplanner.plannerator.planner.dssl.token.operator;
public class RightShiftOperator extends Operator{
    public RightShiftOperator(){
        super(">>");
    }
    @Override
    public Operator newInstance(){
        return new RightShiftOperator();
    }
}