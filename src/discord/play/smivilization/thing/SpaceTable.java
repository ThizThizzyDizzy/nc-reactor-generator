package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.PlacementPoint;
import discord.play.smivilization.Wall;
import java.util.ArrayList;
import java.util.UUID;
public class SpaceTable extends HutThing{
    public SpaceTable(UUID uuid, Hut hut){
        super(uuid, hut, "Space Table", "space table", 24);
        mirrorIf = 1;
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new SpaceTable(uuid, hut);
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
        return 560;
    }
    @Override
    public float getRenderHeight(){
        return 428;
    }
    @Override
    public float getRenderOriginX(){
        return 330;
    }
    @Override
    public float getRenderOriginY(){
        return 410;
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