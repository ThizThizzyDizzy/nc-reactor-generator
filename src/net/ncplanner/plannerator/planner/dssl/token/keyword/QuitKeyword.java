package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
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
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}