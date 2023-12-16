package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class RepeatKeyword extends Keyword{
    public RepeatKeyword(){
        super("repeat");
    }
    @Override
    public Keyword newInstance(){
        return new RepeatKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}