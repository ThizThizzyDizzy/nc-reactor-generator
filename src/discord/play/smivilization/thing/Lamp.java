package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import java.awt.Color;
import java.util.UUID;
import planner.Core;
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
    public void render(int width, int height){
        Core.applyColor(Color.white);
        Renderer2D.drawRect(0, 0, width, height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/lamp/switch casing.png"));
        Renderer2D.drawRect(0, 0, width, height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/lamp/switch "+(on?"on":"off")+".png"));
        Renderer2D.drawRect(0, 0, width, height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/lamp/lamp.png"));
        if(on)Renderer2D.drawRect(0, 0, width, height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/lamp/lamp glow.png"));
    }
    
}