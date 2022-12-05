package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class HasValueKeyword extends Keyword{
    public HasValueKeyword(){
        super("hasvalue");
    }
    @Override
    public Keyword newInstance(){
        return new HasValueKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        script.push(new StackBool(script.pop().asDict().getValue().containsValue(elem.getBaseObject())));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.COLLECTION;
    }
}