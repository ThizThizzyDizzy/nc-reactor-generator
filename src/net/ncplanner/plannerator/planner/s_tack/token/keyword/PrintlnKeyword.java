package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
public class PrintlnKeyword extends Keyword{
    public PrintlnKeyword(){
        super("println");
    }
    @Override
    public Keyword newInstance(){
        return new PrintlnKeyword();
    }
    @Override
    public void run(Script script){
        script.print(script.pop().getValue().toString()+"\n");
    }
}