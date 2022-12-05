package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
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
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}