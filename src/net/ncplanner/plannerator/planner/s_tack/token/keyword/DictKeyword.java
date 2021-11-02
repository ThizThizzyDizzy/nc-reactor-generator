package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class DictKeyword extends Keyword{
    public DictKeyword(){
        super("dict");
    }
    @Override
    public Keyword newInstance(){
        return new DictKeyword();
    }
}