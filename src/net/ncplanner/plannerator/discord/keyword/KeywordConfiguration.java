package net.ncplanner.plannerator.discord.keyword;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import java.util.ArrayList;
import net.ncplanner.plannerator.discord.Keyword;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.configuration.CannedConfiguration;
import net.ncplanner.plannerator.planner.configuration.ConfigurationManager;
public class KeywordConfiguration extends Keyword{
    public CannedConfiguration config;
    public KeywordConfiguration(){
        super("Configuration");
    }
    @Override
    public boolean doRead(String input){
        for(CannedConfiguration c : ConfigurationManager.configurations){
            for(String s : c.aliases){
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
        for(CannedConfiguration c : ConfigurationManager.configurations)options.addAll(c.aliases);
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