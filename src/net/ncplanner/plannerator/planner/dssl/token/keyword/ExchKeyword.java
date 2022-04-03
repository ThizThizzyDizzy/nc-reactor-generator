package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
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
        StackObject oldTop = script.pop();
        StackObject newTop = script.pop();
        script.push(oldTop);
        script.push(newTop);
    }
}