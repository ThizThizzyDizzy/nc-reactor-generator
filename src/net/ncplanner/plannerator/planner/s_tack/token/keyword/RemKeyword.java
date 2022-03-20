package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
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
}