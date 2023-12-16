package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class NativeKeyword extends Keyword{
    public NativeKeyword(){
        super("native");
    }
    @Override
    public Keyword newInstance(){
        return new NativeKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}