package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackClassInstance;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
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
        StackObject obj = script.pop();
        if(obj.getBaseType()==StackObject.Type.CLASS_INSTANCE){
            StackClassInstance instance = (StackClassInstance)obj.getBaseValue();
            script.push(instance);
            script.subscript(instance.castToString());
            script.subscript(() -> {
                script.print(script.pop().getValue().toString()+"\n");
            });
            return;
        }
        script.print(obj.getValue().toString()+"\n");
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}