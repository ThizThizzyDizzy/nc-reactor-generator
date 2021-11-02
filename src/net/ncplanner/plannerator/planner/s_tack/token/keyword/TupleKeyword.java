package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class TupleKeyword extends Keyword{
    public TupleKeyword(){
        super("tuple");
    }
    @Override
    public Keyword newInstance(){
        return new TupleKeyword();
    }
}