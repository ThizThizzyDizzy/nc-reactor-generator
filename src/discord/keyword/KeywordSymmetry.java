package discord.keyword;
import discord.Keyword;
import planner.core.Color;
public class KeywordSymmetry extends Keyword{
    public String symmetry;
    public KeywordSymmetry(){
        super("Symmetry");
    }
    @Override
    public boolean doRead(String input){
        this.symmetry = input;
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(127, 255, 0);
    }
    @Override
    public String getRegex(){
        return "([xyz]{1,3}[ -])?symmetr(y|ical)";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordSymmetry();
    }
    @Override
    public boolean caseSensitive(){
        return false;
    }
}