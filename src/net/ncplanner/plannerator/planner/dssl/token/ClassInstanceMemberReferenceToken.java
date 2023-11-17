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
public class ClassInstanceMemberReferenceToken extends Token{
    public String identifier;
    public ClassInstanceMemberReferenceToken(){
        super("."+name);
    }
    @Override
    public Token newInstance(){
        return new ClassInstanceMemberReferenceToken();
    }
    @Override
    public void load(){
        identifier = text.substring(1);
    }
    @Override
    public void run(Script script){
        StackObject obj = script.pop().getBaseObject();
        if(obj.getType()==StackObject.Type.CLASS_INSTANCE){//instance access
            StackClassInstance instance = (StackClassInstance) obj;
            StackVariable var = instance.script.variables.get(identifier);
            if(var==null)throw new NullPointerException("S'tack variable "+text+" does not exist!");
            if(var instanceof StackMacro){//run the macro immediately
                script.push(obj);//don't pop the instance
                script.subscript(var.getValue().asMethod().getValue());
                return;
            }
            script.push(var.getValue());
            return;
        }
        if(obj.getType()==StackObject.Type.LABEL){//instance variable access
            StackVariable var = obj.asLabel().getVariable();
            StackClassInstance instance = (StackClassInstance) var.getValue();
            StackLabel lbl = new StackLabel(identifier, instance.script);
            script.push(lbl);
            return;
        }
        throw new IllegalArgumentException("Invalid argument for class member reference: "+obj.toString());
    }
}