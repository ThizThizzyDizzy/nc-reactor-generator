package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackSet;
public class KeysKeyword extends Keyword{
    public KeysKeyword(){
        super("keys");
    }
    @Override
    public Keyword newInstance(){
        return new KeysKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackSet(script.pop().asDict().getValue().keySet()));
    }
}