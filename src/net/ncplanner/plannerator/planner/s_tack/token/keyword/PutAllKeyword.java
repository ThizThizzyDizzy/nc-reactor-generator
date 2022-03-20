package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class PutAllKeyword extends Keyword{
    public PutAllKeyword(){
        super("putall");
    }
    @Override
    public Keyword newInstance(){
        return new PutAllKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        script.pop().asCollection().putAll(elem);
    }
}