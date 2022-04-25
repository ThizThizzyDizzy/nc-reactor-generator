package net.ncplanner.plannerator.discord.keyword;
import java.util.Locale;
import net.ncplanner.plannerator.discord.Keyword;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
public class KeywordFuel extends Keyword{
    public String fuel;
    public KeywordFuel(){
        super("Fuel");
    }
    @Override
    public boolean doRead(String input){
        this.fuel = input;
        if(fuel.toLowerCase(Locale.ROOT).contains("[ox]")){
            fuel = fuel.replaceAll("\\[[oO][xX]\\]", "")+" Oxide";
        }
        if(fuel.toLowerCase(Locale.ROOT).contains("[ni]")){
            fuel = fuel.replaceAll("\\[[nN][iI]\\]", "")+" Nitride";
        }
        if(fuel.toLowerCase(Locale.ROOT).contains("[za]")){
            fuel = fuel.replaceAll("\\[[zZ][aA]\\]", "")+"-Zirconium Alloy";
        }
        if(fuel.toLowerCase(Locale.ROOT).contains("[f4]")){
            fuel = fuel.replaceAll("\\[[fF]4\\]", "")+" Fluoride";
        }
        fuel = fuel.replace("  ", " ").replace(" -", "-").trim();
        fuel = fuel.replaceAll("[iI][cC]2[- ]", "IC2 ");
        if(fuel.equalsIgnoreCase("enriched uranium"))fuel = "IC2 "+fuel;
        if(fuel.equalsIgnoreCase("yellorium"))fuel = fuel+" Ingot";
        if(fuel.matches("\\w+ \\d+"))fuel = fuel.replace(" ", "-");
        return true;
    }
    @Override
    public Color getColor(){
        return Core.theme.getKeywordColorFuel();
    }
    @Override
    public String getRegex(){
        return "(ic2[ -]mox|[a-z]+[ -]?\\d{3} ?\\[[a-z0-9]+\\]|\\[[a-z0-9]+\\] ?[a-z]+[ -]?\\d{3}|([a-z]+[ -]?\\d{3}|tbu)([ -]?(ox(ide)?|ni(tride)?|(tetra)?f(4|luoride)|z(a|irconium([ -]?alloy)?)))?)([ -](fuel))?|((ic2[ -]?)?(enriched )?[a-z]+([ -]?(ox(ide)?|ni(tride)?|(tetra)?f(4|luoride)|z(a|irconium([ -]?alloy)?))?)([ -]((nuclear )?fuel|ingot)))|yellorium|(enriched[ -])?uranium";
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