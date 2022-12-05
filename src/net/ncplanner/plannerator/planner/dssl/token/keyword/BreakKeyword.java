package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackFlow;
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
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}