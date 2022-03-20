package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackType;
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