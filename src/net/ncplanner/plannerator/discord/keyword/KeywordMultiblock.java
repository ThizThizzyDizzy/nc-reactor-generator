package net.ncplanner.plannerator.discord.keyword;
import java.util.Locale;
import net.ncplanner.plannerator.discord.Keyword;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
public class KeywordMultiblock extends Keyword{
    public String text;
    public KeywordMultiblock(){
        super("Multiblock");
    }
    public Multiblock getMultiblock(){
        Multiblock multiblock = null;
        for(Multiblock m : Core.multiblockTypes){
            if(m.getDefinitionName().equalsIgnoreCase(text))multiblock = m;
        }
        return multiblock;
    }
    @Override
    public boolean doRead(String input){
        text = input.replaceFirst("((?<!pre[ -])(?<!pre)overhaul)", "Overhaul")
                    .replaceFirst("(pre[ -]?over|under)haul", "Underhaul");
        text = text.toLowerCase(Locale.ROOT).replace(" ", "").replace("-", "").replace("reactor", "").replace("solidfueled", "sfr").replace("moltensalt", "msr");
        if(!text.startsWith("Underhaul")&&!text.startsWith("Overhaul"))text = "Underhaul"+text;
        text = text.replaceFirst("haul", "haul ");
        return true;
    }
    @Override
    public Color getColor(){
        return Core.theme.getKeywordColorMultiblock();
    }
    @Override
    public String getRegex(){
        return "(((pre[ -]?)?over|under)haul )?(turbine|(sfr|msr|solid[ -]?fueled|molten[ -]?salt)( ?reactor)?|(reactor))";
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