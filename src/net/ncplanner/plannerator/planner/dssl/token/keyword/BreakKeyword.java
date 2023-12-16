package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class BreakKeyword extends Keyword{
    public BreakKeyword(){
        super("break");
    }
    @Override
    public Keyword newInstance(){
        return new BreakKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}