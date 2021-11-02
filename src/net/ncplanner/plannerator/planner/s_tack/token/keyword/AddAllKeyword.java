package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class AddAllKeyword extends Keyword{
    public AddAllKeyword(){
        super("addall");
    }
    @Override
    public Keyword newInstance(){
        return new AddAllKeyword();
    }
}