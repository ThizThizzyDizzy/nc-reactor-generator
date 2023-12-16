package net.ncplanner.plannerator.planner.dssl.token.operator;
public class ConcatOperator extends Operator{
    public ConcatOperator(){
        super("~");
    }
    @Override
    public Operator newInstance(){
        return new ConcatOperator();
    }
}