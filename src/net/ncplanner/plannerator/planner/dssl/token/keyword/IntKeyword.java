package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class IntKeyword extends Keyword{
    public IntKeyword(){
        super("int");
    }
    @Override
    public Keyword newInstance(){
        return new IntKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}