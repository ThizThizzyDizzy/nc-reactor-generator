package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class CountKeyword extends Keyword{
    public CountKeyword(){
        super("count");
    }
    @Override
    public Keyword newInstance(){
        return new CountKeyword();
    }
}