package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class ContainsAllKeyword extends Keyword{
    public ContainsAllKeyword(){
        super("containsall");
    }
    @Override
    public Keyword newInstance(){
        return new ContainsAllKeyword();
    }
}