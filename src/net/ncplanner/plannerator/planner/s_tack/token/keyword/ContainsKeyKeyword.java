package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class ContainsKeyKeyword extends Keyword{
    public ContainsKeyKeyword(){
        super("containskey");
    }
    @Override
    public Keyword newInstance(){
        return new ContainsKeyKeyword();
    }
}