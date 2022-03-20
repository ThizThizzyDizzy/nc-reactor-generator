package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
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
}