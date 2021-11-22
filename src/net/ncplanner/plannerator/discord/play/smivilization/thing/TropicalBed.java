package net.ncplanner.plannerator.discord.play.smivilization.thing;
import java.util.UUID;
import net.ncplanner.plannerator.discord.play.smivilization.Hut;
import net.ncplanner.plannerator.discord.play.smivilization.HutThing;
import net.ncplanner.plannerator.discord.play.smivilization.HutThingColorable;
import net.ncplanner.plannerator.discord.play.smivilization.Wall;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
public class TropicalBed extends HutThingColorable{
    public TropicalBed(UUID uuid, Hut hut){
        super(uuid, hut, "Tropical Bed", "tropical bed", 36, Color.WHITE);
        mirrorIf = 1;
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new TropicalBed(uuid, hut);
    }
    @Override
    public void draw(Renderer renderer, float left, float top, float right, float bottom){
        renderer.setColor(Color.WHITE);
        renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/tropical bed/frame.png", left, top, right, bottom);
        renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/tropical bed/matress.png", left, top, right, bottom);
        renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/tropical bed/pillow.png", left, top, right, bottom);
        renderer.setColor(getColor());
        renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/tropical bed/sheets.png", left, top, right, bottom);
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