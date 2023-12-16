package net.ncplanner.plannerator.planner.dssl.token.operator;
public class RightShiftEqualsOperator extends AbstractEqualsOperator{
    public RightShiftEqualsOperator(){
        super(">>=");
    }
    @Override
    public Operator newInstance(){
        return new RightShiftEqualsOperator();
    }
}