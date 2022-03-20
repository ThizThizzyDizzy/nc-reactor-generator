package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class GetKeyword extends Keyword{
    public GetKeyword(){
        super("get");
    }
    @Override
    public Keyword newInstance(){
        return new GetKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        script.push(script.pop().asCollection().get(elem));
    }
}