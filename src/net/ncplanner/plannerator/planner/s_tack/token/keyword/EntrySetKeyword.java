package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class EntrySetKeyword extends Keyword{
    public EntrySetKeyword(){
        super("entryset");
    }
    @Override
    public Keyword newInstance(){
        return new EntrySetKeyword();
    }
}