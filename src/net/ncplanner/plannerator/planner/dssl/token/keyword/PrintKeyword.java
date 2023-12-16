package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class PrintKeyword extends Keyword{
    public PrintKeyword(){
        super("print");
    }
    @Override
    public Keyword newInstance(){
        return new PrintKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}