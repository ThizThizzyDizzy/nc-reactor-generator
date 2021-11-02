package net.ncplanner.plannerator.discord.play.smivilization.thing;
import net.ncplanner.plannerator.discord.play.smivilization.Hut;
import net.ncplanner.plannerator.discord.play.smivilization.HutThing;
import net.ncplanner.plannerator.discord.play.smivilization.HutThingColorable;
import net.ncplanner.plannerator.discord.play.smivilization.Wall;
import java.util.UUID;
import simplelibrary.image.Color;
public class SmoreRug extends HutThingColorable{
    public SmoreRug(UUID uuid, Hut hut){
        super(uuid, hut, "S'more Rug", "smore rug", 8, Color.WHITE);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new SmoreRug(uuid, hut);
    }
    @Override
    public int[] getDimensions(){
        return new int[]{2,7,1};
    }
    @Override
    public int[] getDefaultLocation(){
        return new int[]{4,3,0};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.FLOOR;
    }
    @Override
    public float getRenderWidth(){
        return 653;
    }
    @Override
    public float getRenderHeight(){
        return 582;
    }
    @Override
    public float getRenderOriginX(){
        return 360;
    }
    @Override
    public float getRenderOriginY(){
        return 295;
    }
    @Override
    public float getRenderScale(){
        return 1/1.7098858f;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.FLOOR};
    }
    @Override
    public boolean isBackgroundObject(){
        return true;
    }
}