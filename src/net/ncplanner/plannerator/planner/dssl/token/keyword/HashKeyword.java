package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
public class HashKeyword extends Keyword{
    public HashKeyword(){
        super("hash");
    }
    @Override
    public Keyword newInstance(){
        return new HashKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackInt(script.pop().hashCode()));
    }
}