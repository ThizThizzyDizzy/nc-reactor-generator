package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
public class PopKeyword extends Keyword{
    public PopKeyword(){
        super("pop");
    }
    @Override
    public Keyword newInstance(){
        return new PopKeyword();
    }
    @Override
    public void run(Script script){
        script.pop();
    }
}