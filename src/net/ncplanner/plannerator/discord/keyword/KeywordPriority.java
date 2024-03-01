package net.ncplanner.plannerator.discord.keyword;
import net.ncplanner.plannerator.discord.Keyword;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
public class KeywordPriority extends Keyword{
    public KeywordPriority(){
        super("Priority");
    }
    @Override
    public boolean doRead(String input){
        return true;
    }
    @Override
    public Color getColor(){
        return Core.theme.getKeywordColorPriority();
    }
    @Override
    public String getRegex(){
        return "efficien(t|cy)|power|output|irradiat(e|or|ion)|fuel ?usage|cell ?count|(breed(er|ing) ?)?speed|breed(er|ing)";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordPriority();
    }
    @Override
    public boolean caseSensitive(){
        return false;
    }
}