package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class ReadKeyword extends Keyword{
    public ReadKeyword(){
        super("read");
    }
    @Override
    public Keyword newInstance(){
        return new ReadKeyword();
    }
}