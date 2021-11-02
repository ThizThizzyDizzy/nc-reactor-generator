package net.ncplanner.plannerator.discord.play.smivilization.thing;
import net.ncplanner.plannerator.discord.play.smivilization.Hut;
import net.ncplanner.plannerator.discord.play.smivilization.HutThing;
import net.ncplanner.plannerator.discord.play.smivilization.Wall;
import java.util.UUID;
public class PurpleTriflorice extends HutThing{
    public PurpleTriflorice(UUID uuid, Hut hut){
        super(uuid, hut, "Purple Triflorice", "purple triflorice", 4);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new PurpleTriflorice(uuid, hut);
    }
    @Override
    public int[] getDimensions(){
        return new int[]{1,1,2};
    }
    @Override
    public int[] getDefaultLocation(){
        return new int[]{0,0,3};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.FLOOR;
    }
    @Override
    public float getRenderWidth(){
        return 115;
    }
    @Override
    public float getRenderHeight(){
        return 204;
    }
    @Override
    public float getRenderOriginX(){
        return 10;
    }
    @Override
    public float getRenderOriginY(){
        return 182;
    }
    @Override
    public float getRenderScale(){
        return 1/1.0546067f;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.FLOOR};
    }
}