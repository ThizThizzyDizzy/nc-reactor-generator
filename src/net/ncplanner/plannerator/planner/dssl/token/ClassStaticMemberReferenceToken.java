package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackClass;
import net.ncplanner.plannerator.planner.dssl.object.StackClassInstance;
import net.ncplanner.plannerator.planner.dssl.object.StackLabel;
import net.ncplanner.plannerator.planner.dssl.object.StackMacro;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
import net.ncplanner.plannerator.planner.dssl.token.keyword.ExecKeyword;
public class ClassStaticMemberReferenceToken extends Token{
    public String identifier;
    public ClassStaticMemberReferenceToken(){
        super("::"+name);
    }
    @Override
    public Token newInstance(){
        return new ClassStaticMemberReferenceToken();
    }
    @Override
    public void load(){
        identifier = text.substring(2);
    }
    @Override
    public void run(Script script){
        StackObject obj = script.pop().getBaseObject();
        if(obj.getType()==StackObject.Type.CLASS){//static access
            StackVariable var = ((StackClass)obj).script.variables.get(identifier);
            if(var==null)throw new NullPointerException("S'tack variable "+text+" does not exist!");
            if(var instanceof StackMacro){
                //run the macro immediately
                script.push(var.getValue());
                new ExecKeyword().run(script);
                return;
            }
            script.push(var.getValue());
            return;
        }
        throw new IllegalArgumentException("Invalid argument for class member reference: "+obj.toString());
    }
}