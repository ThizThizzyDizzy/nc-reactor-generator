package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class StringKeyword extends Keyword{
    public StringKeyword(){
        super("string");
    }
    @Override
    public Keyword newInstance(){
        return new StringKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}