package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
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
}