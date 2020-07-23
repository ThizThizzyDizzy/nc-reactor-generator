package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordFuel extends Keyword{
    public String fuel;
    public KeywordFuel(){
        super("Fuel");
    }
    @Override
    public boolean doRead(String input){
        this.fuel = input;
        if(fuel.toLowerCase().contains("[ox]")){
            fuel = fuel.replaceAll("\\[[oO][xX]\\]", "")+" Oxide";
        }
        if(fuel.toLowerCase().contains("[ni]")){
            fuel = fuel.replaceAll("\\[[nN][iI]\\]", "")+" Nitride";
        }
        if(fuel.toLowerCase().contains("[za]")){
            fuel = fuel.replaceAll("\\[[zZ][aA]\\]", "")+"-Zirconium Alloy";
        }
        if(fuel.toLowerCase().contains("[f4]")){
            fuel = fuel.replaceAll("\\[[fF]4\\]", "")+" Fluoride";
        }
        fuel = fuel.replace("  ", " ");
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(255, 255, 0);
    }
    @Override
    public String getRegex(){
        return "([a-z]+[ -]?\\d{3} ?\\[[a-z0-9]+\\]|\\[[a-z0-9]+\\] ?[a-z]+[ -]?\\d{3}|([a-z]+[ -]?\\d{3}|tbu)([ -]?(ox(ide)?|ni(tride)?|(tetra)?f(4|luoride)|z(a|irconium([ -]?alloy)?)))?)([ -](fuel))?|((ic2[ -]?)?(enriched )?[a-z]+([ -]?(ox(ide)?|ni(tride)?|(tetra)?f(4|luoride)|z(a|irconium([ -]?alloy)?))?)([ -]((nuclear )?fuel|ingot)))|yellorium|(enriched[ -])?uranium";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordFuel();
    }
    @Override
    public boolean caseSensitive(){
        return false;
    }
}