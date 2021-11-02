package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class CopyKeyword extends Keyword{
    public CopyKeyword(){
        super("copy");
    }
    @Override
    public Keyword newInstance(){
        return new CopyKeyword();
    }
}