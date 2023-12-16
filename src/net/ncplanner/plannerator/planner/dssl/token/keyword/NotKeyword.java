package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class NotKeyword extends Keyword{
    public NotKeyword(){
        super("!");
    }
    @Override
    public Keyword newInstance(){
        return new NotKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}