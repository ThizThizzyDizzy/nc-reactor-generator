package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordUnderhaul extends Keyword{
    public KeywordUnderhaul(){
        super("Underhaul");
    }
    @Override
    public boolean read(String input){
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(0, 0, 255);
    }
    @Override
    public String getRegex(){
        return "[uU][nN][dD][eE][rR][hH][aA][uU][lL]";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordUnderhaul();
    }
}