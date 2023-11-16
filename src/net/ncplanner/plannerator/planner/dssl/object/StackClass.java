package net.ncplanner.plannerator.planner.dssl.object;
import net.ncplanner.plannerator.planner.dssl.Script;
public class StackClass extends StackObject{
    private final String name;
    public final Script script;
    public StackClass(String name, StackMethod method){
        this.name = name;
        this.script = method.getValue();
        script.run(null);
    }
    @Override
    public Type getType(){
        return Type.CLASS;
    }
    @Override
    public Object getValue(){
        return null;
    }
    @Override
    public StackObject duplicate(){
        return this;
    }
}