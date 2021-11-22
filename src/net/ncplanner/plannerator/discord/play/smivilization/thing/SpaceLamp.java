package net.ncplanner.plannerator.discord.play.smivilization.thing;
import java.util.UUID;
import net.ncplanner.plannerator.discord.play.smivilization.Hut;
import net.ncplanner.plannerator.discord.play.smivilization.HutThing;
import net.ncplanner.plannerator.discord.play.smivilization.Wall;
import net.ncplanner.plannerator.graphics.Renderer;
public class SpaceLamp extends HutThing{
    private boolean on = true;
    public SpaceLamp(UUID uuid, Hut hut){
        super(uuid, hut, "Space Lamp", "space lamp", 18);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new SpaceLamp(uuid, hut);
    }
    @Override
    public void draw(Renderer renderer, float left, float top, float right, float bottom){
        renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/space lamp/lamp.png", left, top, right, bottom);
        if(on)renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/space lamp/lamp glow.png", left, top, right, bottom);
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