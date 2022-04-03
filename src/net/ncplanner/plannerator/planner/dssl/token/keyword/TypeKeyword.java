package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackType;
public class TypeKeyword extends Keyword{
    public TypeKeyword(){
        super("type");
    }
    @Override
    public Keyword newInstance(){
        return new TypeKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackType(script.pop()));
    }
}