package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.token.Token;
public abstract class Keyword extends Token{
    public Keyword(String keyword){
        super(keyword);
    }
    @Override
    public abstract Keyword newInstance();
    @Override
    public final void load(){}
    public abstract KeywordFlavor getFlavor();
    public static enum KeywordFlavor{
        KEYWORD, COLLECTION, FLOW, TYPE, STACK;
    }
}