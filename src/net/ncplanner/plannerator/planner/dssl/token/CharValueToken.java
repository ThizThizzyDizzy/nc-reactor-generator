package net.ncplanner.plannerator.planner.dssl.token;
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
        String txt = text;
        txt = txt.replace("\\t", "\t");
        txt = txt.replace("\\b", "\b");
        txt = txt.replace("\\n", "\n");
        txt = txt.replace("\\r", "\r");
        txt = txt.replace("\\f", "\f");
        txt = txt.replace("\\'", "\'");
        txt = txt.replace("\\\"", "\"");
        txt = txt.replace("\\\\", "\\");
        value = txt.charAt(1);
    }
}