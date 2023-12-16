package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class PopKeyword extends Keyword{
    public PopKeyword(){
        super("pop");
    }
    @Override
    public Keyword newInstance(){
        return new PopKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.STACK;
    }
}