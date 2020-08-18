package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.Wall;
import java.util.UUID;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class LightSwitch extends HutThing{
    private boolean on = true;
    public LightSwitch(UUID uuid, Hut hut){
        super(uuid, hut, "Light Switch", "light switch", 2);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new LightSwitch(uuid, hut);
    }
    @Override
    public void draw(double left, double top, double right, double bottom){
        Renderer2D.drawRect(left, top, right, bottom, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/lamp/switch casing.png"));
        Renderer2D.drawRect(left, top, right, bottom, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/lamp/switch "+(on?"on":"off")+".png"));
    }
    @Override
    public int[] getDimensions(){
        return new int[]{1,1,1};
    }
    @Override
    public int[] getDefaultLocation(){
        return  new int[]{0,9,4};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.LEFT;
    }
    @Override
    public float getRenderWidth(){
        return 118;
    }
    @Override
    public float getRenderHeight(){
        return 257;
    }
    @Override
    public float getRenderOriginX(){
        return 36;
    }
    @Override
    public float getRenderOriginY(){
        return 123;
    }
    @Override
    public float getRenderScale(){
        return 1/2.0375257f;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.LEFT,Wall.RIGHT};
    }
}