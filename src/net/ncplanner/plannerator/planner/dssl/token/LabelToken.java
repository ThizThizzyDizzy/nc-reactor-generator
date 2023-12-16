package net.ncplanner.plannerator.planner.dssl.token;
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
}