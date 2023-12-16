package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class BoolKeyword extends Keyword{
    public BoolKeyword(){
        super("bool");
    }
    @Override
    public Keyword newInstance(){
        return new BoolKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}