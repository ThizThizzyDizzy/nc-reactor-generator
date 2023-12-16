package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class InterpretKeyword extends Keyword{
    public InterpretKeyword(){
        super("interpret");
    }
    @Override
    public Keyword newInstance(){
        return new InterpretKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}