package net.ncplanner.plannerator.planner.dssl.token.operator;
public class XOrEqualsOperator extends AbstractEqualsOperator{
    public XOrEqualsOperator(){
        super("^=");
    }
    @Override
    public Operator newInstance(){
        return new XOrEqualsOperator();
    }
}