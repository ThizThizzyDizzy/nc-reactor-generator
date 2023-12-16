package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class DerefKeyword extends Keyword{
    public DerefKeyword(){
        super("deref");
    }
    @Override
    public Keyword newInstance(){
        return new DerefKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}