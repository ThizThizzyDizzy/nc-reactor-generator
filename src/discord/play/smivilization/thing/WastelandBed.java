package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.HutThingColorable;
import discord.play.smivilization.Wall;
import simplelibrary.image.Color;
import java.util.UUID;
import planner.Core;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class WastelandBed extends HutThingColorable{
    public WastelandBed(UUID uuid, Hut hut){
        super(uuid, hut, "Wasteland Bed", "wasteland bed", 48, Color.WHITE);
        mirrorIf = 1;
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new WastelandBed(uuid, hut);
    }
    @Override
    public void draw(double left, double top, double right, double bottom){
        Core.applyColor(Color.WHITE);
        Renderer2D.drawRect(left, top, right, bottom, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/wasteland bed/frame.png"));
        Core.applyColor(getColor());
        Renderer2D.drawRect(left, top, right, bottom, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/wasteland bed/matress.png"));
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
        return 770;
    }
    @Override
    public float getRenderOriginX(){
        return 355;
    }
    @Override
    public float getRenderOriginY(){
        return 470;
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