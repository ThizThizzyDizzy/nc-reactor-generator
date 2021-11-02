package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class StringKeyword extends Keyword{
    public StringKeyword(){
        super("string");
    }
    @Override
    public Keyword newInstance(){
        return new StringKeyword();
    }
}