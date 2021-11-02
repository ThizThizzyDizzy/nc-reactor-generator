package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class ValSetKeyword extends Keyword{
    public ValSetKeyword(){
        super("valset");
    }
    @Override
    public Keyword newInstance(){
        return new ValSetKeyword();
    }
}