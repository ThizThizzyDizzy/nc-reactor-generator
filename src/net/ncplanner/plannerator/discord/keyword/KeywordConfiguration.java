package net.ncplanner.plannerator.discord.keyword;
import java.util.ArrayList;
import net.ncplanner.plannerator.discord.Keyword;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
public class KeywordConfiguration extends Keyword{
    public Configuration config;
    public KeywordConfiguration(){
        super("Configuration");
    }
    @Override
    public boolean doRead(String input){
        for(Configuration c : Configuration.configurations){
            for(String s : c.alternatives){
                if(input.equalsIgnoreCase(s)){
                    config = c;
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public Color getColor(){
        return Core.theme.getKeywordColorConfiguration();
    }
    @Override
    public String getRegex(){
        ArrayList<String> options = new ArrayList<>();
        for(Configuration c : Configuration.configurations)options.addAll(c.alternatives);
        if(options.isEmpty())return null;
        return String.join("|", options);
    }
    @Override
    public Keyword newInstance(){
        return new KeywordConfiguration();
    }
    @Override
    public boolean caseSensitive(){
        return false;
    }
}