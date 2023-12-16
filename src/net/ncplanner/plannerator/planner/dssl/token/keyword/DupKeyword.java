package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class DupKeyword extends Keyword{
    public DupKeyword(){
        super("dup");
    }
    @Override
    public Keyword newInstance(){
        return new DupKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.STACK;
    }
}