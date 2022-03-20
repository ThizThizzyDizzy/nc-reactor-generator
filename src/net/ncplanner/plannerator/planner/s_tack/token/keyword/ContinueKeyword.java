package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackFlow;
public class ContinueKeyword extends Keyword{
    public ContinueKeyword(){
        super("continue");
    }
    @Override
    public Keyword newInstance(){
        return new ContinueKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackFlow(StackFlow.Flow.CONTINUE));
    }
}