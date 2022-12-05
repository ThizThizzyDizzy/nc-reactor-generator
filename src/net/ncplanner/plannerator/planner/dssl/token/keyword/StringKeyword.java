package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
public class StringKeyword extends Keyword{
    public StringKeyword(){
        super("string");
    }
    @Override
    public Keyword newInstance(){
        return new StringKeyword();
    }
    @Override
    public void run(Script script){
        script.push(script.pop().asString());
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}