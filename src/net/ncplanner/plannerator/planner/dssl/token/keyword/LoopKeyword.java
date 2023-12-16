package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class LoopKeyword extends Keyword{
    public LoopKeyword(){
        super("loop");
    }
    @Override
    public Keyword newInstance(){
        return new LoopKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}