package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackSet;
public class ValuesKeyword extends Keyword{
    public ValuesKeyword(){
        super("values");
    }
    @Override
    public Keyword newInstance(){
        return new ValuesKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackSet(script.pop().asDict().getValue().values()));
    }
}