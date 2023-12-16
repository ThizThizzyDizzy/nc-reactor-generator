package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class SetKeyword extends Keyword{
    public SetKeyword(){
        super("set");
    }
    @Override
    public Keyword newInstance(){
        return new SetKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}