package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class PrintlnKeyword extends Keyword{
    public PrintlnKeyword(){
        super("println");
    }
    @Override
    public Keyword newInstance(){
        return new PrintlnKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}