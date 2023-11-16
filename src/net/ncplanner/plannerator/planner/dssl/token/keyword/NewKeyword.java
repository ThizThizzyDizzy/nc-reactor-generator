package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackClass;
import net.ncplanner.plannerator.planner.dssl.object.StackClassInstance;
public class NewKeyword extends Keyword{
    public NewKeyword(){
        super("new");
    }
    @Override
    public Keyword newInstance(){
        return new NewKeyword();
    }
    @Override
    public void run(Script script){
        StackClassInstance instance = new StackClassInstance((StackClass)script.pop());
        script.push(instance);
        script.subscript(instance.script);
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}