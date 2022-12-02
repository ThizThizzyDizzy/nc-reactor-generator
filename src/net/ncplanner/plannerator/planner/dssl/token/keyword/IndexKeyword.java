package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
public class IndexKeyword extends Keyword{
    public IndexKeyword(){
        super("index");
    }
    @Override
    public Keyword newInstance(){
        return new IndexKeyword();
    }
    @Override
    public void run(Script script){
        int val = script.pop().asInt().getValue().intValue();
        if(val<0)throw new IllegalArgumentException("value must be non-negative!");
        script.push(script.peekAt(script.stack.size()-val-1));
    }
}