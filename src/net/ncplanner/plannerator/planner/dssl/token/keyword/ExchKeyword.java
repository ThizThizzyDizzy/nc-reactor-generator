package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class ExchKeyword extends Keyword{
    public ExchKeyword(){
        super("exch");
    }
    @Override
    public Keyword newInstance(){
        return new ExchKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.STACK;
    }
}