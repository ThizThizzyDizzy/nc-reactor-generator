package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordOverhaul extends Keyword{
    public KeywordOverhaul(){
        super("Underhaul");
    }
    @Override
    public boolean read(String input){
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(255, 127, 0);
    }
    @Override
    public String getRegex(){
        return "[oO][vV][eE][rR][hH][aA][uU][lL]";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordOverhaul();
    }
}