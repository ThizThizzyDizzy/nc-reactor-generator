package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class HasAllKeyword extends Keyword{
    public HasAllKeyword(){
        super("hasall");
    }
    @Override
    public Keyword newInstance(){
        return new HasAllKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        script.push(new StackBool(script.pop().asCollection().containsAll(elem.asCollection().collection())));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.COLLECTION;
    }
}