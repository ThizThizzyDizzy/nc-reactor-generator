package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.HutThingColorable;
import discord.play.smivilization.Wall;
import java.util.UUID;
import planner.Core;
import simplelibrary.image.Color;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class Bed extends HutThingColorable{
    public Bed(UUID uuid, Hut hut){
        super(uuid, hut, "Bed", "bed", 24, Color.WHITE);
        mirrorIf = 1;
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new Bed(uuid, hut);
    }
    @Override
    public void draw(double left, double top, double right, double bottom){
        Core.applyColor(Color.WHITE);
        Renderer2D.drawRect(left, top, right, bottom, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/bed/frame.png"));
        Renderer2D.drawRect(left, top, right, bottom, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/bed/matress.png"));
        Renderer2D.drawRect(left, top, right, bottom, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/bed/pillow.png"));
        Core.applyColor(getColor());
        Renderer2D.drawRect(left, top, right, bottom, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/bed/sheets.png"));
    }
    @Override
    public int[] getDimensions(){
        return new int[]{3,7,2};
    }
    @Override
    public int[] getDefaultLocation(){
        return new int[]{0,3,0};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.FLOOR;
    }
    @Override
    public float getRenderWidth(){
        return 771;
    }
    @Override
    public float getRenderHeight(){
        return 670;
    }
    @Override
    public float getRenderOriginX(){
        return 355;
    }
    @Override
    public float getRenderOriginY(){
        return 420;
    }
    @Override
    public float getRenderScale(){
        return 1/1.7098858f;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.FLOOR};
    }
}