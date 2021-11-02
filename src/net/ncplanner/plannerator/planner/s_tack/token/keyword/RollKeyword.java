package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class RollKeyword extends Keyword{
    public RollKeyword(){
        super("roll");
    }
    @Override
    public Keyword newInstance(){
        return new RollKeyword();
    }
}