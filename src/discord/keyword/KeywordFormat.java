package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordFormat extends Keyword{
    public KeywordFormat(){
        super("Output Format");
    }
    @Override
    public boolean doRead(String input){
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(0, 0, 0);
    }
    @Override
    public String getRegex(){
        return "((hellrage ?)?json|ncpf)( ?format)?";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordFormat();
    }
    @Override
    public boolean caseSensitive(){
        return false;
    }
}