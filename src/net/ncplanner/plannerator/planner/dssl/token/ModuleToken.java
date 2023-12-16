package net.ncplanner.plannerator.planner.dssl.token;
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
}