package discord.play.smivilization.thing.special;
import discord.play.SmoreBot;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import java.awt.Color;
import java.util.UUID;
import planner.Core;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class SmoreTrophy extends HutThing{
    public SmoreTrophy(UUID uid, Hut hut){
        super(uid, hut, "S'more Trophy", "trophy", -1);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new SmoreTrophy(uuid, hut);
    }
    @Override
    public void render(int width, int height){
        Core.applyColor(Color.white);
        String type;
        switch(SmoreBot.getSmorePlacement(hut.owner)){
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
                return;
        }
        Renderer2D.drawRect(0, 0, width, height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/trophy/"+type+".png"));
    }
}