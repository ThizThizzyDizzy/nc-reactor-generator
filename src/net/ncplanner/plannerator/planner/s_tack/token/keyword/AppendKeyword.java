package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class AppendKeyword extends Keyword{
    public AppendKeyword(){
        super("append");
    }
    @Override
    public Keyword newInstance(){
        return new AppendKeyword();
    }
}