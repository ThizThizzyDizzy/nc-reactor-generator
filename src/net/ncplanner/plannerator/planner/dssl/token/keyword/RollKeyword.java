package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.token.Helpers;
public class RollKeyword extends Keyword{
    public RollKeyword(){
        super("roll");
    }
    @Override
    public Keyword newInstance(){
        return new RollKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.STACK;
    }
}