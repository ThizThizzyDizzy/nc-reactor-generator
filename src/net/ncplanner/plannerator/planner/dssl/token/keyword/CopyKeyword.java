package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
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
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.STACK;
    }
}