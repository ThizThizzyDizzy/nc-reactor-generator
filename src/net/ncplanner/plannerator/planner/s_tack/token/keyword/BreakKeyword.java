package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackFlow;
public class BreakKeyword extends Keyword{
    public BreakKeyword(){
        super("break");
    }
    @Override
    public Keyword newInstance(){
        return new BreakKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackFlow(StackFlow.Flow.BREAK));
    }
}