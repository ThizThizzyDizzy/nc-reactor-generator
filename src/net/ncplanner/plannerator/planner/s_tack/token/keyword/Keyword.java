package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.token.Token;
public abstract class Keyword extends Token{
    public Keyword(String keyword){
        super(keyword);
    }
    @Override
    public abstract Keyword newInstance();
    @Override
    public final void load(){}
}