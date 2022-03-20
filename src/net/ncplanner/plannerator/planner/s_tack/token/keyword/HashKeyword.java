package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
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