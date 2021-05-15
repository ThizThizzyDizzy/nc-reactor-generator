package discord.keyword;
import discord.Keyword;
import planner.Core;
import simplelibrary.image.Color;
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
        return Core.theme.getKeywordColorFormat();
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