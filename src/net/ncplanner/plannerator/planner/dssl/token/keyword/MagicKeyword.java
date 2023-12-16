package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class MagicKeyword extends Keyword{
    public MagicKeyword(){
        super("magic");
    }
    @Override
    public Keyword newInstance(){
        return new MagicKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}