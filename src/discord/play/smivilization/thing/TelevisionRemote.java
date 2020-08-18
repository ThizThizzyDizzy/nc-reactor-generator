package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.Wall;
import java.util.UUID;
public class TelevisionRemote extends HutThing{
    public TelevisionRemote(UUID uuid, Hut hut){
        super(uuid, hut, "TV Remote", "tv remote", 4);
        mirrorIf = -1;
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new TelevisionRemote(uuid, hut);
    }
    @Override
    public int[] getDimensions(){
        return new int[]{1,1,1};
    }
    @Override
    public int[] getDefaultLocation(){
        return  new int[]{6,0,0};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.FLOOR;
    }
    @Override
    public float getRenderWidth(){
        return 98;
    }
    @Override
    public float getRenderHeight(){
        return 65;
    }
    @Override
    public float getRenderOriginX(){
        return 20;
    }
    @Override
    public float getRenderOriginY(){
        return 30;
    }
    @Override
    public float getRenderScale(){
        return 1/1.1638198f;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.FLOOR};
    }
}