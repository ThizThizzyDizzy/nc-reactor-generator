package discord;
import java.awt.Color;
public abstract class Keyword{
    public final String name;
    public Keyword(String name){
        this.name = name;
    }
    public abstract boolean read(String input);
    public abstract Color getColor();
    public abstract String getRegex();
    public abstract Keyword newInstance();
}