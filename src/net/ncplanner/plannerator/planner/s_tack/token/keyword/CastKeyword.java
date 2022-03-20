package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class CastKeyword extends Keyword{
    public CastKeyword(){
        super("cast");
    }
    @Override
    public Keyword newInstance(){
        return new CastKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        script.push(elem.asType().internal.cast(script.pop()));
    }
}