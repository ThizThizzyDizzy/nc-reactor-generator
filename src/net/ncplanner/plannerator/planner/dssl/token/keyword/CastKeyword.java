package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class CastKeyword extends Keyword{
    public CastKeyword(){
        super("cast");
    }
    @Override
    public Keyword newInstance(){
        return new CastKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}