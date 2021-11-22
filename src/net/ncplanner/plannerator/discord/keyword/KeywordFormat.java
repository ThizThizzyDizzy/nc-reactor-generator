package net.ncplanner.plannerator.discord.keyword;
import net.ncplanner.plannerator.discord.Keyword;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.graphics.image.Color;
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