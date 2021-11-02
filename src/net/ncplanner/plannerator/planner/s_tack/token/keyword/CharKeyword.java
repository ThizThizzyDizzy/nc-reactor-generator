package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class CharKeyword extends Keyword{
    public CharKeyword(){
        super("char");
    }
    @Override
    public Keyword newInstance(){
        return new CharKeyword();
    }
}