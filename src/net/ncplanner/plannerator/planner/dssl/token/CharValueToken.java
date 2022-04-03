package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackChar;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class CharValueToken extends Token{
    public char value;
    public CharValueToken(){
        super(apostrophe+c_char+apostrophe);
    }
    @Override
    public Token newInstance(){
        return new CharValueToken();
    }
    @Override
    public void load(){
        value = text.charAt(1);
    }
    @Override
    public void run(Script script){
        script.push(new StackChar(value));
    }
}