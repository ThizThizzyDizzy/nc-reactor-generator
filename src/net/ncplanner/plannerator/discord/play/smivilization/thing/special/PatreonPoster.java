package net.ncplanner.plannerator.discord.play.smivilization.thing.special;
import java.util.UUID;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.discord.play.smivilization.Hut;
import net.ncplanner.plannerator.discord.play.smivilization.HutThing;
import net.ncplanner.plannerator.discord.play.smivilization.HutThingExclusive;
import net.ncplanner.plannerator.discord.play.smivilization.Wall;
public class PatreonPoster extends HutThingExclusive{
    int frameType = 1;
    public PatreonPoster(UUID uuid, Hut hut){
        super(uuid, hut, "Patreon Poster", "special/patreon/poster", 210445638532333569L, -1);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new PatreonPoster(uuid, hut);
    }
    @Override
    public void render(Renderer renderer, float imgScale){
        renderer.setWhite();
        float[] lefttop = Hut.convertXYZtoXY512(x, y, z+getDimZ());
        float right = Hut.convertXYZtoXY512(x+getDimX(), y, z+getDimZ())[0];
        float bottom = (right-lefttop[0])/getRenderWidth()*getRenderHeight()+lefttop[1];
        renderer.drawImage("/textures/smivilization/buildings/huts/gliese/furniture/special/patreon.png", lefttop[0], lefttop[1], right, bottom);
    }
    @Override
    public int[] getDimensions(){
        if(wall==Wall.BACK)return new int[]{3,1,4};
        else return new int[]{1,3,4};
    }
    @Override
    public int[] getDefaultLocation(){    
        return new int[]{0,0,5};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.BACK;
    }
    @Override
    public float getRenderWidth(){
        return 2476;
    }
    @Override
    public float getRenderHeight(){
        return 3234;
    }
    @Override
    public float getRenderOriginX(){
        return 0;
    }
    @Override
    public float getRenderOriginY(){
        return 0;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.BACK};
    }
    @Override
    public boolean isBackgroundObject(){
        return false;
    }
}