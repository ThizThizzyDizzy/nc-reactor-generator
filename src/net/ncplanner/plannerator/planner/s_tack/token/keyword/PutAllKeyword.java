package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class PutAllKeyword extends Keyword{
    public PutAllKeyword(){
        super("putall");
    }
    @Override
    public Keyword newInstance(){
        return new PutAllKeyword();
    }
}