package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.Wall;
import java.util.UUID;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class Lamp extends HutThing{
    private boolean on = true;
    public Lamp(UUID uuid, Hut hut){
        super(uuid, hut, "Lamp", "lamp", 18);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new Lamp(uuid, hut);
    }
    @Override
    public void draw(double left, double top, double right, double bottom){
        Renderer2D.drawRect(left, top, right, bottom, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/lamp/lamp.png"));
        if(on)Renderer2D.drawRect(left, top, right, bottom, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/lamp/lamp glow.png"));
    }
    @Override
    public int[] getDimensions(){
        return new int[]{2,2,2};
    }
    @Override
    public int[] getDefaultLocation(){
        return new int[]{4,6,9};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.CIELING;
    }
    @Override
    public float getRenderWidth(){
        return 809;
    }
    @Override
    public float getRenderHeight(){
        return 679;
    }
    @Override
    public float getRenderOriginX(){
        return 407;
    }
    @Override
    public float getRenderOriginY(){
        return 70;
    }
    @Override
    public float getRenderScale(){
        return 1/1.7644925f;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.CIELING};
    }
    @Override
    public boolean isLamp(){
        return true;
    }
    @Override
    public boolean isOn(){
        return on;
    }
    @Override
    public void setOn(boolean on){
        this.on = on;
    }
}