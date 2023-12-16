package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class IfKeyword extends Keyword{
    public IfKeyword(){
        super("if");
    }
    @Override
    public Keyword newInstance(){
        return new IfKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}