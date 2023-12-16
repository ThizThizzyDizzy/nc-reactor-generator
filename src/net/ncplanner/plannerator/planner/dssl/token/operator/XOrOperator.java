package net.ncplanner.plannerator.planner.dssl.token.operator;
public class XOrOperator extends Operator{
    public XOrOperator(){
        super("^");
    }
    @Override
    public Operator newInstance(){
        return new XOrOperator();
    }
}