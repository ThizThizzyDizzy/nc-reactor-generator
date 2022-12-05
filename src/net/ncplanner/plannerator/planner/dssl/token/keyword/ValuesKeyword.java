package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackSet;
public class ValuesKeyword extends Keyword{
    public ValuesKeyword(){
        super("values");
    }
    @Override
    public Keyword newInstance(){
        return new ValuesKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackSet(script.pop().asDict().getValue().values()));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.COLLECTION;
    }
}