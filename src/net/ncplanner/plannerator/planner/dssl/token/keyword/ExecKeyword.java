package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class ExecKeyword extends Keyword{
    public ExecKeyword(){
        super("exec");
    }
    @Override
    public Keyword newInstance(){
        return new ExecKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}