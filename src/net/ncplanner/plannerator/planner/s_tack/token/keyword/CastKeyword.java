package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class CastKeyword extends Keyword{
    public CastKeyword(){
        super("cast");
    }
    @Override
    public Keyword newInstance(){
        return new CastKeyword();
    }
}