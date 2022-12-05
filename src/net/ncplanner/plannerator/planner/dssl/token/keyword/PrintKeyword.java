package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
public class PrintKeyword extends Keyword{
    public PrintKeyword(){
        super("print");
    }
    @Override
    public Keyword newInstance(){
        return new PrintKeyword();
    }
    @Override
    public void run(Script script){
        script.print(script.pop().getValue().toString());
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}