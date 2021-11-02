package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class InvKeyword extends Keyword{
    public InvKeyword(){
        super("inv");
    }
    @Override
    public Keyword newInstance(){
        return new InvKeyword();
    }
}