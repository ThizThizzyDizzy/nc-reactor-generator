package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackSet;
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