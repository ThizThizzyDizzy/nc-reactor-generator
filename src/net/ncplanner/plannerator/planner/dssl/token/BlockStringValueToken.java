package net.ncplanner.plannerator.planner.dssl.token;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class BlockStringValueToken extends Token{
    public String value;
    public BlockStringValueToken(){
        super(multi_quote+whitespace+"*"+eol+b_char_sequence+multi_quote);
    }
    @Override
    public Token newInstance(){
        return new BlockStringValueToken();
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
        value = txt.substring(1, txt.length()-1);
    }
}