package net.ncplanner.plannerator.discord.play.smivilization.thing;
import java.util.UUID;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.discord.play.smivilization.Hut;
import net.ncplanner.plannerator.discord.play.smivilization.HutThing;
import net.ncplanner.plannerator.discord.play.smivilization.HutThingColorable;
import net.ncplanner.plannerator.discord.play.smivilization.Wall;
import simplelibrary.image.Color;
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
    public void draw(Renderer renderer, double left, double top, double right, double bottom){
        renderer.setColor(Color.WHITE);
        renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/bed/frame.png", left, top, right, bottom);
        renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/bed/matress.png", left, top, right, bottom);
        renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/bed/pillow.png", left, top, right, bottom);
        renderer.setColor(getColor());
        renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/bed/sheets.png", left, top, right, bottom);
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