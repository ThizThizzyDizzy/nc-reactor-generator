package net.ncplanner.plannerator.planner.dssl.token.operator;
public class ModuloEqualsOperator extends AbstractEqualsOperator{
    public ModuloEqualsOperator(){
        super("%%=");
    }
    @Override
    public Operator newInstance(){
        return new ModuloEqualsOperator();
    }
}