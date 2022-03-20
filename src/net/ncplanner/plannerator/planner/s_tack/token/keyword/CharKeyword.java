package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
public class CharKeyword extends Keyword{
    public CharKeyword(){
        super("char");
    }
    @Override
    public Keyword newInstance(){
        return new CharKeyword();
    }
    @Override
    public void run(Script script){
        script.push(script.pop().asChar());
    }
}