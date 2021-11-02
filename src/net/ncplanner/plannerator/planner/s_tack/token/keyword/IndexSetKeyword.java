package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class IndexSetKeyword extends Keyword{
    public IndexSetKeyword(){
        super("indexset");
    }
    @Override
    public Keyword newInstance(){
        return new IndexSetKeyword();
    }
}