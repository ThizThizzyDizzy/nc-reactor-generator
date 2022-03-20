package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class HasKeyKeyword extends Keyword{
    public HasKeyKeyword(){
        super("haskey");
    }
    @Override
    public Keyword newInstance(){
        return new HasKeyKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        script.push(new StackBool(script.pop().asDict().getValue().containsKey(elem.getBaseObject())));
    }
}