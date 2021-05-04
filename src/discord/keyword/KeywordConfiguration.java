package discord.keyword;
import discord.Keyword;
import multiblock.configuration.Configuration;
import simplelibrary.image.Color;
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
        return new Color(255, 0, 255);
    }
    @Override
    public String getRegex(){
        return "po3|e2e|aapn|sf4";
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