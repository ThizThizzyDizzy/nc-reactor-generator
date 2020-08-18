package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordMultiblock extends Keyword{
    public String text;
    public KeywordMultiblock(){
        super("Multiblock");
    }
    @Override
    public boolean doRead(String input){
        text = input.toLowerCase().replace(" ", "").replace("-", "").replace("reactor", "").replace("solidfueled", "sfr").replace("moltensalt", "msr");
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(0, 255, 0);
    }
    @Override
    public String getRegex(){
        return "turbine|(sfr|msr|solid[ -]?fueled|molten[ -]?salt)( ?reactor)?|(reactor)";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordMultiblock();
    }
    @Override
    public boolean caseSensitive(){
        return false;
    }
}