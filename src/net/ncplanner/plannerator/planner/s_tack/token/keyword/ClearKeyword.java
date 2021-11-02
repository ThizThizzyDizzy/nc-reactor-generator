package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class ClearKeyword extends Keyword{
    public ClearKeyword(){
        super("clear");
    }
    @Override
    public Keyword newInstance(){
        return new ClearKeyword();
    }
}