package discord.keyword;
import discord.Keyword;
import generator.Priority;
import java.util.ArrayList;
import multiblock.Multiblock;
import planner.Core;
import simplelibrary.image.Color;
public class KeywordPriority extends Keyword{
    public KeywordPriority(){
        super("Priority");
    }
    @Override
    public boolean doRead(String input){
        for(Multiblock m : Core.multiblockTypes){
            ArrayList<Priority.Preset> presets = new ArrayList<>();
            m.getGenerationPriorityPresets(m.getGenerationPriorities(), presets);
            for(Priority.Preset preset : presets){
                for(String s : (ArrayList<String>)preset.alternatives){
                    if(input.equalsIgnoreCase(s))return true;
                }
            }
        }
        return false;
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