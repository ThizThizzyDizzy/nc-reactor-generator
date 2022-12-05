package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class RemAllKeyword extends Keyword{
    public RemAllKeyword(){
        super("remall");
    }
    @Override
    public Keyword newInstance(){
        return new RemAllKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        script.push(script.pop().asCollection().removeAll(elem.asCollection().collection()));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.COLLECTION;
    }
}