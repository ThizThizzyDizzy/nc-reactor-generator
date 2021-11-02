package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class CountToKeyword extends Keyword{
    public CountToKeyword(){
        super("countto");
    }
    @Override
    public Keyword newInstance(){
        return new CountToKeyword();
    }
}