package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class CopyKeyword extends Keyword{
    public CopyKeyword(){
        super("copy");
    }
    @Override
    public Keyword newInstance(){
        return new CopyKeyword();
    }
    @Override
    public void run(Script script){
        int val = script.pop().asInt().getValue().intValue();
        if(val<0)throw new IllegalArgumentException("value must be non-negative!");
        for(StackObject obj : script.peek(val))script.push(obj);
    }
}