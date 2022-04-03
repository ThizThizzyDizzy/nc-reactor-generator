package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
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