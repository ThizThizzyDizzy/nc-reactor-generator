package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class ListKeyword extends Keyword{
    public ListKeyword(){
        super("list");
    }
    @Override
    public Keyword newInstance(){
        return new ListKeyword();
    }
}