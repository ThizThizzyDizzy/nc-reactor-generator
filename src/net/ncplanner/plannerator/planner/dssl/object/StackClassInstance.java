package net.ncplanner.plannerator.planner.dssl.object;
import net.ncplanner.plannerator.planner.dssl.Script;
public class StackClassInstance extends StackObject{
    private final StackClass parent;
    public Script script;
    public StackClassInstance(StackClass parent){
        this.parent = parent;
        script = new Script(parent.script.variables.get(Script.VAR_PREFIX_MAGIC+"init").asMethod().getValue().script, parent.script.out);
        for(String key : parent.script.variables.keySet()){
            script.variables.put(key, parent.script.variables.get(key).duplicateVariable());
        }
    }
    @Override
    public Type getType(){
        return Type.CLASS_INSTANCE;
    }
    @Override
    public StackObject getValue(){
        return this;
    }
    @Override
    public StackObject duplicate(){
        return this;
    }
}