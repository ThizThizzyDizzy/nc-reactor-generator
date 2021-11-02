package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class IndexGetKeyword extends Keyword{
    public IndexGetKeyword(){
        super("indexget");
    }
    @Override
    public Keyword newInstance(){
        return new IndexGetKeyword();
    }
}