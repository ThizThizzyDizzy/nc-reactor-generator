package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class CharKeyword extends Keyword{
    public CharKeyword(){
        super("char");
    }
    @Override
    public Keyword newInstance(){
        return new CharKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}