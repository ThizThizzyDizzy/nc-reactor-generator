package net.ncplanner.plannerator.discord.play.smivilization.thing;
import net.ncplanner.plannerator.discord.play.smivilization.Hut;
import net.ncplanner.plannerator.discord.play.smivilization.HutThing;
import net.ncplanner.plannerator.discord.play.smivilization.PlacementPoint;
import net.ncplanner.plannerator.discord.play.smivilization.Wall;
import java.util.ArrayList;
import java.util.UUID;
public class TropicalShelf extends HutThing{
    public TropicalShelf(UUID uuid, Hut hut){
        super(uuid, hut, "Tropical Shelf", "tropical shelf", 18);
        mirrorIf = -1;
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new TropicalShelf(uuid, hut);
    }
    @Override
    public int[] getDimensions(){
        return new int[]{1,8,4};
    }
    @Override
    public int[] getDefaultLocation(){
        return new int[]{9,2,6};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.RIGHT;
    }
    @Override
    public float getRenderWidth(){
        return 627;
    }
    @Override
    public float getRenderHeight(){
        return 718;
    }
    @Override
    public float getRenderOriginX(){
        return 345;
    }
    @Override
    public float getRenderOriginY(){
        return 280;
    }
    @Override
    public float getRenderScale(){
        return 1/1.6552793f;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.LEFT,Wall.RIGHT};
    }
    @Override
    public void getPlacementPoints(ArrayList<PlacementPoint> points){
        addHorizontalPlacementPointGrid(Wall.FLOOR, x, y, z, getDimX(), getDimY(), points);
        addHorizontalPlacementPointGrid(Wall.FLOOR, x, y, z+2, getDimX(), getDimY(), points);
    }
}