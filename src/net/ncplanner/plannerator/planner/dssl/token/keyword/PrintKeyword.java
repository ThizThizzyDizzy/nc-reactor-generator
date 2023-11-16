package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackClassInstance;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
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
        StackObject obj = script.pop();
        if(obj.getBaseType()==StackObject.Type.CLASS_INSTANCE){
            StackClassInstance instance = (StackClassInstance)obj.getBaseValue();
            script.push(instance);
            script.subscript(instance.script.variables.get(Script.VAR_PREFIX_MAGIC+"str").asMethod().getValue());
            script.subscript(() -> {
                script.print(script.pop().getValue().toString());
            });
            return;
        }
        script.print(obj.getValue().toString());
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}