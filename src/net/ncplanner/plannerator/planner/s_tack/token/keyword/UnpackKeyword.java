package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class UnpackKeyword extends Keyword{
    public UnpackKeyword(){
        super("unpack");
    }
    @Override
    public Keyword newInstance(){
        return new UnpackKeyword();
    }
}