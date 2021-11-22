package net.ncplanner.plannerator.discord.keyword;
import net.ncplanner.plannerator.discord.Keyword;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
public class KeywordCuboid extends Keyword{
    public int x,y,z;
    public KeywordCuboid(){
        super("Cuboid");
    }
    @Override
    public String toString(){
        return "Cuboid: "+x+"x"+y+"x"+z;
    }
    @Override
    public Color getColor(){
        return Core.theme.getKeywordColorCuboid();
    }
    @Override
    public String getRegex(){
        return "\\d+x\\d+x\\d+";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordCuboid();
    }
    @Override
    public boolean doRead(String input){
        String[] split = input.split("x");
        x = Integer.parseInt(split[0]);
        y = Integer.parseInt(split[1]);
        z = Integer.parseInt(split[2]);
        return true;
    }
    @Override
    public boolean caseSensitive(){
        return false;
    }
}