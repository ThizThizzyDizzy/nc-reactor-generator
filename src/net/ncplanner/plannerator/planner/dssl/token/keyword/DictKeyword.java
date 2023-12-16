package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class DictKeyword extends Keyword{
    public DictKeyword(){
        super("dict");
    }
    @Override
    public Keyword newInstance(){
        return new DictKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}