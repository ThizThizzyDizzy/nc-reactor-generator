package net.ncplanner.plannerator.discord.keyword;
import net.ncplanner.plannerator.discord.Keyword;
import net.ncplanner.plannerator.planner.Core;
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
        return Core.theme.getKeywordColorUnderhaul();
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