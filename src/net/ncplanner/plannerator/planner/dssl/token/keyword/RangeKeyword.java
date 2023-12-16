package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class RangeKeyword extends Keyword{
    public RangeKeyword(){
        super("range");
    }
    @Override
    public Keyword newInstance(){
        return new RangeKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}