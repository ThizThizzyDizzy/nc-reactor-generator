package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class ContainsKeyword extends Keyword{
    public ContainsKeyword(){
        super("contains");
    }
    @Override
    public Keyword newInstance(){
        return new ContainsKeyword();
    }
}