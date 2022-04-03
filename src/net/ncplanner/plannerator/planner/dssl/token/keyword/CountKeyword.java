package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
public class CountKeyword extends Keyword{
    public CountKeyword(){
        super("count");
    }
    @Override
    public Keyword newInstance(){
        return new CountKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackInt(script.stack.size()));
    }
}