package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
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
}