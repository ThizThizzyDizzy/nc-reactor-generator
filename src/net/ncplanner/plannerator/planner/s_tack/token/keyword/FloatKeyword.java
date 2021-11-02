package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class FloatKeyword extends Keyword{
    public FloatKeyword(){
        super("float");
    }
    @Override
    public Keyword newInstance(){
        return new FloatKeyword();
    }
}