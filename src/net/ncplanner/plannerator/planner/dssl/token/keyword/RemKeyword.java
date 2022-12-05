package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class RemKeyword extends Keyword{
    public RemKeyword(){
        super("rem");
    }
    @Override
    public Keyword newInstance(){
        return new RemKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        script.push(script.pop().asCollection().remove(elem));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.COLLECTION;
    }
}