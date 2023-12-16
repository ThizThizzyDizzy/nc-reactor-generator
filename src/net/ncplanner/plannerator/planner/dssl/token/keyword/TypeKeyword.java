package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class TypeKeyword extends Keyword{
    public TypeKeyword(){
        super("type");
    }
    @Override
    public Keyword newInstance(){
        return new TypeKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}