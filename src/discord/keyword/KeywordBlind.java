package discord.keyword;
import discord.Keyword;
import planner.core.Color;
public class KeywordBlind extends Keyword{
    public KeywordBlind(){
        super("Blind");
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
        return "blind";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordBlind();
    }
    @Override
    public boolean caseSensitive(){
        return false;
    }
}