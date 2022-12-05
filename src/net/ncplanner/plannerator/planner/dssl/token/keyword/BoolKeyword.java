package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
public class BoolKeyword extends Keyword{
    public BoolKeyword(){
        super("bool");
    }
    @Override
    public Keyword newInstance(){
        return new BoolKeyword();
    }
    @Override
    public void run(Script script){
        script.push(script.pop().asBool());
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}