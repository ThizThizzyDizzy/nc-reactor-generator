package discord.keyword;
import discord.Keyword;
import planner.Core;
import simplelibrary.image.Color;
public class KeywordSmores extends Keyword{
    public int numSmores;
    public KeywordSmores(){
        super("S'mores");
    }
    @Override
    public boolean doRead(String input){
        numSmores = Integer.parseInt(input.split(" ")[0]);
        return true;
    }
    @Override
    public Color getColor(){
        return Core.theme.getKeywordColorSmores();
    }
    @Override
    public String getRegex(){
        return "[0-9]+ s['’]?mores?";
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