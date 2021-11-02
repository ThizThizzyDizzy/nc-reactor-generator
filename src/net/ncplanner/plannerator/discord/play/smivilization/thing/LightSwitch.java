package net.ncplanner.plannerator.discord.play.smivilization.thing;
import net.ncplanner.plannerator.discord.play.smivilization.Hut;
import net.ncplanner.plannerator.discord.play.smivilization.HutThing;
import net.ncplanner.plannerator.discord.play.smivilization.Wall;
import java.util.UUID;
import net.ncplanner.plannerator.Renderer;
import simplelibrary.opengl.ImageStash;
public class LightSwitch extends HutThing{
    private boolean on = true;
    public LightSwitch(UUID uuid, Hut hut){
        super(uuid, hut, "Light Switch", "light switch", 2);
        mirrorIf = 1;
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new LightSwitch(uuid, hut);
    }
    @Override
    public void draw(Renderer renderer, double left, double top, double right, double bottom){
        renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/lamp/switch casing.png", left, top, right, bottom);
        renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/lamp/switch "+(on?"on":"off")+".png", left, top, right, bottom);
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
    @Override
    public boolean isLightSwitch(){
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