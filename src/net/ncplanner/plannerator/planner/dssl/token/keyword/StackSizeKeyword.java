package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class StackSizeKeyword extends Keyword{
    public StackSizeKeyword(){
        super("stacksize");
    }
    @Override
    public Keyword newInstance(){
        return new StackSizeKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.STACK;
    }
}