package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
public class QuitKeyword extends Keyword{
    public QuitKeyword(){
        super("quit");
    }
    @Override
    public Keyword newInstance(){
        return new QuitKeyword();
    }
    @Override
    public void run(Script script){
        script.halt();
    }
}