package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class IsEmptyKeyword extends Keyword{
    public IsEmptyKeyword(){
        super("isempty");
    }
    @Override
    public Keyword newInstance(){
        return new IsEmptyKeyword();
    }
}