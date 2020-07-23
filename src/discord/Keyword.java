package discord;
import java.awt.Color;
public abstract class Keyword{
    public final String name;
    protected String input;
    public Keyword(String name){
        this.name = name;
    }
    public boolean read(String input){
        this.input = input;
        return doRead(input);
    }
    protected abstract boolean doRead(String input);
    public abstract Color getColor();
    public abstract String getRegex();
    public abstract Keyword newInstance();
    public abstract boolean caseSensitive();
}