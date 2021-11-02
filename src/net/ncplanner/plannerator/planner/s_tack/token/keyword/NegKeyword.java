package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class NegKeyword extends Keyword{
    public NegKeyword(){
        super("neg");
    }
    @Override
    public Keyword newInstance(){
        return new NegKeyword();
    }
}