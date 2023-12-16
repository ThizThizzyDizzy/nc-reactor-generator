package net.ncplanner.plannerator.planner.dssl.token.operator;
public class ModuloOperator extends Operator{
    public ModuloOperator(){
        super("%%");
    }
    @Override
    public Operator newInstance(){
        return new ModuloOperator();
    }
}