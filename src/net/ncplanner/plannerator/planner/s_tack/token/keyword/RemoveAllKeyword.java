package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class RemoveAllKeyword extends Keyword{
    public RemoveAllKeyword(){
        super("removeall");
    }
    @Override
    public Keyword newInstance(){
        return new RemoveAllKeyword();
    }
}