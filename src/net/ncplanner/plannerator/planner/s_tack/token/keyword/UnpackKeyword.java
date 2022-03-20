package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class UnpackKeyword extends Keyword{
    public UnpackKeyword(){
        super("unpack");
    }
    @Override
    public Keyword newInstance(){
        return new UnpackKeyword();
    }
    @Override
    public void run(Script script){
        for(StackObject o : (Iterable<StackObject>)script.pop().asCollection().collection())script.push(o);
    }
}