package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class ForeachKeyword extends Keyword{
    public ForeachKeyword(){
        super("foreach");
    }
    @Override
    public Keyword newInstance(){
        return new ForeachKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}