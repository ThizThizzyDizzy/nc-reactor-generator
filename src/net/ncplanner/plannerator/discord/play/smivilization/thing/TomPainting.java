package net.ncplanner.plannerator.discord.play.smivilization.thing;
import java.util.UUID;
import net.ncplanner.plannerator.discord.play.smivilization.Hut;
import net.ncplanner.plannerator.discord.play.smivilization.HutThing;
import net.ncplanner.plannerator.discord.play.smivilization.Wall;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
public class TomPainting extends HutThing{
    public TomPainting(UUID uuid, Hut hut){
        super(uuid, hut, "tomdodd4598 Painting", "tompainting", 4598);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new TomPainting(uuid, hut);
    }
    @Override
    public void render(Renderer renderer, float imgScale){
        renderer.bindTexture(TextureManager.getImageRaw(getTexture()));
        switch(wall){
            case LEFT:
                quad(renderer,
                        0,0,x,y+getDimY(),z,
                        0,1,x,y+getDimY(),z+getDimZ(),
                        1,0,x,y,z,
                        1,1,x,y,z+getDimZ());
                break;
            case BACK:
                quad(renderer,
                        0, 0, x, y, z,
                        0, 1, x, y, z+getDimZ(),
                        1, 0, x+getDimX(), y, z,
                        1, 1, x+getDimX(), y, z+getDimZ());
                break;
            case RIGHT:
                quad(renderer,
                        0,0,x,y,z,
                        0,1,x,y,z+getDimZ(),
                        1,0,x,y+getDimY(),z,
                        1,1,x,y+getDimY(),z+getDimZ());
                break;
            default:
                throw new IllegalArgumentException("Cannot render on wall "+wall.toString()+"!");
        }
    }
    private void quad(Renderer renderer, float s1, float t1, float x1, float y1, float z1, float s2, float t2, float x2, float y2, float z2, float s3, float t3, float x3, float y3, float z3, float s4, float t4, float x4, float y4, float z4){
        float[] pos1 = Hut.convertXYZtoXY512(x1, y1, z1);
        float[] pos2 = Hut.convertXYZtoXY512(x2, y2, z2);
        float[] pos3 = Hut.convertXYZtoXY512(x3, y3, z3);
        float[] pos4 = Hut.convertXYZtoXY512(x4, y4, z4);
        renderer.drawScreenQuad(pos1[0], pos1[1], pos2[0], pos2[1], pos3[0], pos3[1], pos4[0], pos4[1], 1, s1, t1, s2, t2, s3, t3, s4, t4);
    }
    @Override
    public int[] getDimensions(){
        if(wall==Wall.BACK)return new int[]{6,1,6};
        else return new int[]{1,6,6};
    }
    @Override
    public int[] getDefaultLocation(){
        return new int[]{0,1,3};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.LEFT;
    }
    @Override
    public float getRenderWidth(){
        return 0;
    }
    @Override
    public float getRenderHeight(){
        return 0;
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
        return new Wall[]{Wall.LEFT,Wall.BACK,Wall.RIGHT};
    }
    @Override
    public boolean isBackgroundObject(){
        return true;
    }
}