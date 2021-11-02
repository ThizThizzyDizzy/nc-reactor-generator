package net.ncplanner.plannerator.discord.play.smivilization.thing.special;
import net.ncplanner.plannerator.discord.play.SmoreBot;
import net.ncplanner.plannerator.discord.play.smivilization.Hut;
import net.ncplanner.plannerator.discord.play.smivilization.HutThing;
import net.ncplanner.plannerator.discord.play.smivilization.Wall;
import java.util.UUID;
public class SmoreTrophy extends HutThing{
    public SmoreTrophy(UUID uid, Hut hut){
        super(uid, hut, "S'more Trophy", "trophy", -1);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new SmoreTrophy(uuid, hut);
    }
    @Override
    public String getTexture(){
        String type;
        switch(SmoreBot.getSmorePlacement(hut.parent.owner)){
            case 1:
                type = "gold smore";
                break;
            case 2:
                type = "silver smore";
                break;
            case 3:
                type = "bronze smore";
                break;
            default:
                type = "no trophy";
                break;
        }
        return "/textures/smivilization/buildings/huts/gliese/furniture/trophy/"+type+".png";
    }
    @Override
    public int[] getDimensions(){
        return new int[]{1,1,1};
    }
    @Override
    public int[] getDefaultLocation(){
        return new int[]{4,-1,4};
    }
    @Override
    public Wall getDefaultWall(){
        return Wall.FLOOR;
    }
    @Override
    public float getRenderWidth(){
        return 128;
    }
    @Override
    public float getRenderHeight(){
        return 112;
    }
    @Override
    public float getRenderOriginX(){
        return 64;
    }
    @Override
    public float getRenderOriginY(){
        return 76.5f;
    }
    @Override
    public Wall[] getAllowedWalls(){
        return new Wall[]{Wall.FLOOR};
    }
}