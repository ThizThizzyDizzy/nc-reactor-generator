package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.PlacementPoint;
import discord.play.smivilization.Wall;
import java.util.ArrayList;
import java.util.UUID;
public class Table extends HutThing{
    public Table(UUID uuid, Hut hut){
        super(uuid, hut, "Table", "table", 8);
        mirrorIf = 1;
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new Table(uuid, hut);
    }
    @Override
    public int[] getDimensions(){
        return new int[]{4,2,3};
    }
    @Override
    public int[] getDefaultLocation(){
        return new int[]{0,0,0};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.FLOOR;
    }
    @Override
    public float getRenderWidth(){
        return 540;
    }
    @Override
    public float getRenderHeight(){
        return 408;
    }
    @Override
    public float getRenderOriginX(){
        return 320;
    }
    @Override
    public float getRenderOriginY(){
        return 400;
    }
    @Override
    public float getRenderScale(){
        return 1/1.1092132f;
    }
    @Override
    public float getRenderScaleY(){
        return 1.05f;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.FLOOR};
    }
    @Override
    public void getPlacementPoints(ArrayList<PlacementPoint> points){
        addHorizontalPlacementPointGrid(Wall.FLOOR, x, y, z+getDimZ(), getDimX(), getDimY(), points);
    }
}