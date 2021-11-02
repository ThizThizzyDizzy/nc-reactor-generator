package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class ContainsValKeyword extends Keyword{
    public ContainsValKeyword(){
        super("containsval");
    }
    @Override
    public Keyword newInstance(){
        return new ContainsValKeyword();
    }
}