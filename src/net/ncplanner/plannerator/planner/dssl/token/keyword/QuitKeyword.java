package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class QuitKeyword extends Keyword{
    public QuitKeyword(){
        super("quit");
    }
    @Override
    public Keyword newInstance(){
        return new QuitKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}