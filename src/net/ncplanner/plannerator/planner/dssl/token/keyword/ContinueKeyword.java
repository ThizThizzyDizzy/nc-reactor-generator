package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class ContinueKeyword extends Keyword{
    public ContinueKeyword(){
        super("continue");
    }
    @Override
    public Keyword newInstance(){
        return new ContinueKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}