package net.ncplanner.plannerator.planner.dssl.token.operator;
public class IDivideEqualsOperator extends AbstractEqualsOperator{
    public IDivideEqualsOperator(){
        super("//=");
    }
    @Override
    public Operator newInstance(){
        return new IDivideEqualsOperator();
    }
}