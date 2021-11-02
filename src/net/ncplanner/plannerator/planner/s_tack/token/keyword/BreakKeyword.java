package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class BreakKeyword extends Keyword{
    public BreakKeyword(){
        super("break");
    }
    @Override
    public Keyword newInstance(){
        return new BreakKeyword();
    }
}