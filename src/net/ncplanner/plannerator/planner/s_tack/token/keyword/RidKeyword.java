package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class RidKeyword extends Keyword{
    public RidKeyword(){
        super("rid");
    }
    @Override
    public Keyword newInstance(){
        return new RidKeyword();
    }
}