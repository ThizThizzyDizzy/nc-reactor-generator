package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackNull;
public class NullKeyword extends Keyword{
    public NullKeyword(){
        super("null");
    }
    @Override
    public Keyword newInstance(){
        return new NullKeyword();
    }
    @Override
    public void run(Script script){
        script.push(StackNull.INSTANCE);
    }
}