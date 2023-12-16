package net.ncplanner.plannerator.planner.dssl.token.operator;
public class IDivideOperator extends Operator{
    public IDivideOperator(){
        super("//");
    }
    @Override
    public Operator newInstance(){
        return new IDivideOperator();
    }
}