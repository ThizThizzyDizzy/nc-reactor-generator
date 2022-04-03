package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
public class CloneKeyword extends Keyword{
    public CloneKeyword(){
        super("clone");
    }
    @Override
    public Keyword newInstance(){
        return new CloneKeyword();
    }
    @Override
    public void run(Script script){
        script.push(script.peek().duplicate());
    }
}