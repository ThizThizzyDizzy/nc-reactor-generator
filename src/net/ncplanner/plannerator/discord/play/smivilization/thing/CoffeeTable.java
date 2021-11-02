package net.ncplanner.plannerator.discord.play.smivilization.thing;
import java.util.ArrayList;
import java.util.UUID;
import net.ncplanner.plannerator.discord.play.smivilization.Hut;
import net.ncplanner.plannerator.discord.play.smivilization.HutThing;
import net.ncplanner.plannerator.discord.play.smivilization.PlacementPoint;
import net.ncplanner.plannerator.discord.play.smivilization.Wall;
public class CoffeeTable extends HutThing{
    public CoffeeTable(UUID uuid, Hut hut){
        super(uuid, hut, "Coffee Table", "coffee table", 16);
        mirrorIf = -1;
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new CoffeeTable(uuid, hut);
    }
    @Override
    public int[] getDimensions(){
        return new int[]{5,2,2};
    }
    @Override
    public int[] getDefaultLocation(){
        return new int[]{5,0,0};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.FLOOR;
    }
    @Override
    public float getRenderWidth(){
        return 590;
    }
    @Override
    public float getRenderHeight(){
        return 268;
    }
    @Override
    public float getRenderOriginX(){
        return 265;
    }
    @Override
    public float getRenderOriginY(){
        return 248;
    }
    @Override
    public float getRenderScale(){
        return 1/1.092132f;
    }
    @Override
    public float getRenderScaleY(){
        return 1.15f;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.FLOOR};
    }
    @Override
    public void getPlacementPoints(ArrayList<PlacementPoint> points){
        addHorizontalPlacementPointGrid(Wall.FLOOR, x+1, y, z, getDimX()-1, 1, points);//lower shelf
        addHorizontalPlacementPointGrid(Wall.FLOOR, x, y, z+getDimZ(), getDimX(), getDimY(), points);
    }
}