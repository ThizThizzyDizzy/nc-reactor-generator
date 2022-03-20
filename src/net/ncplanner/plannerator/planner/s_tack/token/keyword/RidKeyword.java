package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
public class RidKeyword extends Keyword{
    public RidKeyword(){
        super("rid");
    }
    @Override
    public Keyword newInstance(){
        return new RidKeyword();
    }
    @Override
    public void run(Script script){
        int val = script.pop().asInt().getValue().intValue();
        if(val<0)throw new IllegalArgumentException("value must be non-negative!");
        for(int i = 0; i<val; i++){
            script.pop();
        }
    }
}