package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class ExchKeyword extends Keyword{
    public ExchKeyword(){
        super("exch");
    }
    @Override
    public Keyword newInstance(){
        return new ExchKeyword();
    }
    @Override
    public void run(Script script){
        StackObject oldTop = script.stack.pop();
        StackObject newTop = script.stack.pop();
        script.stack.push(oldTop);
        script.stack.push(newTop);
    }
}