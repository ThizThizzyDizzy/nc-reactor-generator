package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class IncludeKeyword extends Keyword{
    public IncludeKeyword(){
        super("include");
    }
    @Override
    public Keyword newInstance(){
        return new IncludeKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}