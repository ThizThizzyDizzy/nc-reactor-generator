package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class NullKeyword extends Keyword{
    public NullKeyword(){
        super("null");
    }
    @Override
    public Keyword newInstance(){
        return new NullKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}