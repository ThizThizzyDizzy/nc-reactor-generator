package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class DefKeyword extends Keyword{
    public DefKeyword(){
        super("def");
    }
    @Override
    public Keyword newInstance(){
        return new DefKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}