package discord.play.smivilization.thing.special;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.HutThingExclusive;
import discord.play.smivilization.Wall;
import java.util.UUID;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
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
    public void render(float imgScale){
        GL11.glColor4d(1, 1, 1, 1);
        double[] lefttop = Hut.convertXYZtoXY512(x, y, z+getDimZ());
        double right = Hut.convertXYZtoXY512(x+getDimX(), y, z+getDimZ())[0];
        double bottom = (right-lefttop[0])/getRenderWidth()*getRenderHeight()+lefttop[1];
//        Renderer2D.drawRect(lefttop[0], lefttop[1], right, bottom, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/special/patreon.png"));
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