package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class HasKeyword extends Keyword{
    public HasKeyword(){
        super("has");
    }
    @Override
    public Keyword newInstance(){
        return new HasKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        script.push(new StackBool(script.pop().asCollection().contains(elem)));
    }
}