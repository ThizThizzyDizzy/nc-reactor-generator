package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class MacroKeyword extends Keyword{
    public MacroKeyword(){
        super("macro");
    }
    @Override
    public Keyword newInstance(){
        return new MacroKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}