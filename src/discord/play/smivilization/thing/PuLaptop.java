package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.Wall;
import java.util.UUID;
public class PuLaptop extends HutThing{
    public PuLaptop(UUID uuid, Hut hut){
        super(uuid, hut, "Pu Laptop", "pu laptop", 32);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new PuLaptop(uuid, hut);
    }
    @Override
    public int[] getDimensions(){
        return new int[]{2,2,1};
    }
    @Override
    public int[] getDefaultLocation(){
        return new int[]{2,0,3};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.FLOOR;
    }
    @Override
    public float getRenderWidth(){
        return 240;
    }
    @Override
    public float getRenderHeight(){
        return 222;
    }
    @Override
    public float getRenderOriginX(){
        return 158;
    }
    @Override
    public float getRenderOriginY(){
        return 165;
    }
    @Override
    public float getRenderScale(){
        return 1/1.1092132f;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.FLOOR};
    }
}