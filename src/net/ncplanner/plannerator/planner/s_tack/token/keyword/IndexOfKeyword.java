package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class IndexOfKeyword extends Keyword{
    public IndexOfKeyword(){
        super("indexof");
    }
    @Override
    public Keyword newInstance(){
        return new IndexOfKeyword();
    }
}