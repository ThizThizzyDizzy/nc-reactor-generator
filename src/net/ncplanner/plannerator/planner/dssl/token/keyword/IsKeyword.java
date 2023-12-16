package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class IsKeyword extends Keyword{
    public IsKeyword(){
        super("is");
    }
    @Override
    public Keyword newInstance(){
        return new IsKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}