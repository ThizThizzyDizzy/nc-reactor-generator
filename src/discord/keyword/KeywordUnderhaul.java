package discord.keyword;
import discord.Keyword;
import simplelibrary.image.Color;
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
        return "(pre[ -]?over|under)haul";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordUnderhaul();
    }
    @Override
    public boolean caseSensitive(){
        return false;
    }
}