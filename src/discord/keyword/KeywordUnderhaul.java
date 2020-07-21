package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordUnderhaul extends Keyword{
    public KeywordUnderhaul(){
        super("Underhaul");
    }
    @Override
    public boolean doRead(String input){
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(0, 0, 255);
    }
    @Override
    public String getRegex(){
        return "([pP][rR][eE][ -]?[oO][vV][eE][rR]|[uU][nN][dD][eE][rR])[hH][aA][uU][lL]";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordUnderhaul();
    }
}