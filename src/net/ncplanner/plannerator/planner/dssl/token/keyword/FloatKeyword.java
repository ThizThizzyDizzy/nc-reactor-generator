package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class FloatKeyword extends Keyword{
    public FloatKeyword(){
        super("float");
    }
    @Override
    public Keyword newInstance(){
        return new FloatKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}