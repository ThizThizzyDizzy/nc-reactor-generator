package discord.keyword;
import discord.Keyword;
import planner.core.Color;
public class KeywordOverhaul extends Keyword{
    public KeywordOverhaul(){
        super("Overhaul");
    }
    @Override
    public boolean doRead(String input){
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(255, 127, 0);
    }
    @Override
    public String getRegex(){
        return "((?<!pre[ -])(?<!pre)overhaul)";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordOverhaul();
    }
    @Override
    public boolean caseSensitive(){
        return false;
    }
}