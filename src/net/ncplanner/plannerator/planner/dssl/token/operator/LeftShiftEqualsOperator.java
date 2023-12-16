package net.ncplanner.plannerator.planner.dssl.token.operator;
public class LeftShiftEqualsOperator extends AbstractEqualsOperator{
    public LeftShiftEqualsOperator(){
        super("<<=");
    }
    @Override
    public Operator newInstance(){
        return new LeftShiftEqualsOperator();
    }
}