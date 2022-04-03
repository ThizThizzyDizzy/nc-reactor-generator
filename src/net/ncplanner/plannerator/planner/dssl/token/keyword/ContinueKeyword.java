package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackFlow;
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