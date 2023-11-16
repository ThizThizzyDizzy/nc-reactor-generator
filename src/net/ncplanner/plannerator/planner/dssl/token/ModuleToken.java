package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackModule;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class ModuleToken extends Token{
    public String module;
    public ModuleToken(){
        super("$"+name);
    }
    @Override
    public Token newInstance(){
        return new ModuleToken();
    }
    @Override
    public void load(){
        module = text.substring(1);
    }
    @Override
    public void run(Script script){
        script.push(new StackModule(module));
    }
}