package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
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
}