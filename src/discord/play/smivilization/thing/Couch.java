package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.HutThingColorable;
import discord.play.smivilization.Wall;
import java.awt.Color;
import java.util.UUID;
public class Couch extends HutThingColorable{
    public Couch(UUID uuid, Hut hut){
        super(uuid, hut, "Couch", "couch", 16, Color.red);
        mirrorIf = -1;
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new Couch(uuid, hut);
    }
    @Override
    public int[] getDimensions(){
        return new int[]{3,7,4};
    }
    @Override
    public int[] getDefaultLocation(){
        return new int[]{7,3,0};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.FLOOR;
    }
    @Override
    public float getRenderWidth(){
        return 164;
    }
    @Override
    public float getRenderHeight(){
        return 237;
    }
    @Override
    public float getRenderOriginX(){
        return 67;
    }
    @Override
    public float getRenderOriginY(){
        return 174;
    }
    @Override
    public float getRenderScale(){
        return 4/1.7098858f;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.FLOOR};
    }
}