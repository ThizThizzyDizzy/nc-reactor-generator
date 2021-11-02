package net.ncplanner.plannerator.discord.keyword;
import net.ncplanner.plannerator.discord.Keyword;
import net.ncplanner.plannerator.planner.Core;
import simplelibrary.image.Color;
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
        return Core.theme.getKeywordColorBlind();
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