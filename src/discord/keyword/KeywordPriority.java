package discord.keyword;
import discord.Keyword;
import generator.Priority;
import java.awt.Color;
import java.util.ArrayList;
import multiblock.Multiblock;
import planner.Core;
public class KeywordPriority extends Keyword{
    public KeywordPriority(){
        super("Priority");
    }
    @Override
    public boolean read(String input){
        for(Multiblock m : Core.multiblockTypes){
            ArrayList<Priority.Preset> presets = new ArrayList<>();
            m.getGenerationPriorityPresets(m.getGenerationPriorities(), presets);
            for(Priority.Preset preset : presets){
                for(String s : preset.alternatives){
                    if(input.equalsIgnoreCase(s))return true;
                }
            }
        }
        return false;
    }
    @Override
    public Color getColor(){
        return new Color(0, 255, 127);
    }
    @Override
    public String getRegex(){
        return "[eE][fF]{2}[iI][cC][iI][eE][nN]([tT]|[cC][yY])|[oO][uU][tT][pP][uU][tT]|[iI][rR]{2}[aA][dD][iI][aA][tT]([eE]|[oO][rR]|[iI][oO][nN])|[fF][uU][eE][lL] ?[uU][sS][aA][gG][eE]|[cC][eE][lL]{2} ?[cC][oO][uU][nN][tT]|([bB][rR][eE]{2}[dD]([eE][rR]|[iI][nN][gG]) ?)?[sS][pP][eE][eE][dD]|[bB][rR][eE]{2}[dD]([eE][rR]|[iI][nN][gG])";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordPriority();
    }
}