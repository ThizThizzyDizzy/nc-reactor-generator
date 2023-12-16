package net.ncplanner.plannerator.planner.dssl.token;
import java.util.regex.Pattern;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.separator;
public abstract class Token{
    public final String regex;
    public String text;
    private Pattern pattern;
    public int start;//starting position index in the script itself; for debugging
    public Token(String regex){
        this(regex, false);
    }
    public Token(String regex, boolean plain){
        this.regex = plain?regex:("(?<=^|"+separator+")"+regex+"(?=$|"+separator+")");
    }
    public abstract Token newInstance();
    public Token newInstance(String text){
        Token token = newInstance();
        token.text = text;
        token.load();
        return token;
    }
    public void load(){}
    public Pattern getStartPattern(){
        if(pattern!=null)return pattern;
        return pattern = Pattern.compile("^"+regex);
    }
}