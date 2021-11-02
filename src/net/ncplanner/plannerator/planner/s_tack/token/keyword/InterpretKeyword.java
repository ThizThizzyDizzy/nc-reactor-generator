package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class InterpretKeyword extends Keyword{
    public InterpretKeyword(){
        super("interpret");
    }
    @Override
    public Keyword newInstance(){
        return new InterpretKeyword();
    }
}