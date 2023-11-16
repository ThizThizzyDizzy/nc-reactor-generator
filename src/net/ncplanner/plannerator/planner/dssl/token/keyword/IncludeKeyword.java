package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
public class IncludeKeyword extends Keyword{
    public IncludeKeyword(){
        super("include");
    }
    @Override
    public Keyword newInstance(){
        return new IncludeKeyword();
    }
    @Override
    public void run(Script script){
        throw new UnsupportedOperationException("Not yet implemented");
        //import internal module
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}