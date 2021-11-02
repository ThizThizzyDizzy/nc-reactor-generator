package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class IntKeyword extends Keyword{
    public IntKeyword(){
        super("int");
    }
    @Override
    public Keyword newInstance(){
        return new IntKeyword();
    }
}