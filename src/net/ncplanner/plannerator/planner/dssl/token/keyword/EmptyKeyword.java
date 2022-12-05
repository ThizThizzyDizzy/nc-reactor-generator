package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
public class EmptyKeyword extends Keyword{
    public EmptyKeyword(){
        super("empty");
    }
    @Override
    public Keyword newInstance(){
        return new EmptyKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackBool(script.pop().asCollection().isEmpty()));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.COLLECTION;
    }
}