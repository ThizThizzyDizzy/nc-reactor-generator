package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class NewKeyword extends Keyword{
    public NewKeyword(){
        super("new");
    }
    @Override
    public Keyword newInstance(){
        return new NewKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}