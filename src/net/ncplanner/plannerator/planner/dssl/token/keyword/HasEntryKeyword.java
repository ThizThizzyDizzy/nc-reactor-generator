package net.ncplanner.plannerator.planner.dssl.token.keyword;
import java.util.HashMap;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class HasEntryKeyword extends Keyword{
    public HasEntryKeyword(){
        super("hasentry");
    }
    @Override
    public Keyword newInstance(){
        return new HasEntryKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem2 = script.pop();
        StackObject elem1 = script.pop();
        HashMap<StackObject, StackObject> dict = script.pop().asDict().getValue();
        script.push(new StackBool(dict.containsKey(elem1.getBaseObject())&&dict.get(elem1.getBaseObject()).equals(elem2.getBaseObject())));
    }
}