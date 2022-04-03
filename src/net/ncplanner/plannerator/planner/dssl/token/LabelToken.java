package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackLabel;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class LabelToken extends Token{
    public String label;
    public LabelToken(){
        super("/"+name);
    }
    @Override
    public Token newInstance(){
        return new LabelToken();
    }
    @Override
    public void load(){
        label = text.substring(1);
    }
    @Override
    public void run(Script script){
        script.push(new StackLabel(label));
    }
}