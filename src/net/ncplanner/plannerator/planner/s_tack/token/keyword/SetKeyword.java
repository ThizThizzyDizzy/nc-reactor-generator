package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class SetKeyword extends Keyword{
    public SetKeyword(){
        super("set");
    }
    @Override
    public Keyword newInstance(){
        return new SetKeyword();
    }
}