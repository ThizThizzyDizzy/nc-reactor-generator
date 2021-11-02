package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class RemoveKeyword extends Keyword{
    public RemoveKeyword(){
        super("remove");
    }
    @Override
    public Keyword newInstance(){
        return new RemoveKeyword();
    }
}