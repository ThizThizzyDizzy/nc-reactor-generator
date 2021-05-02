package discord.keyword;
import discord.Keyword;
import simplelibrary.image.Color;
import java.util.Locale;
import multiblock.Multiblock;
import planner.Core;
public class KeywordMultiblock extends Keyword{
    public String text;
    public KeywordMultiblock(){
        super("Multiblock");
    }
    public Multiblock getMultiblock(boolean overhaul){
        String fullMultiblockName = (overhaul?"Overhaul ":"Underhaul ")+text.toUpperCase(Locale.ENGLISH);
        Multiblock multiblock = null;
        for(Multiblock m : Core.multiblockTypes){
            if(m.getDefinitionName().equalsIgnoreCase(fullMultiblockName))multiblock = m;
        }
        return multiblock;
    }
    @Override
    public boolean doRead(String input){
        text = input.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("-", "").replace("reactor", "").replace("solidfueled", "sfr").replace("moltensalt", "msr");
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