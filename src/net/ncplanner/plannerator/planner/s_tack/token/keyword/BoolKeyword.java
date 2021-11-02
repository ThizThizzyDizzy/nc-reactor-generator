package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class BoolKeyword extends Keyword{
    public BoolKeyword(){
        super("bool");
    }
    @Override
    public Keyword newInstance(){
        return new BoolKeyword();
    }
}