package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class HaltKeyword extends Keyword{
    public HaltKeyword(){
        super("halt");
    }
    @Override
    public Keyword newInstance(){
        return new HaltKeyword();
    }
}