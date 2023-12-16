package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class ListKeyword extends Keyword{
    public ListKeyword(){
        super("list");
    }
    @Override
    public Keyword newInstance(){
        return new ListKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}