package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
import java.awt.Color;
import planner.Core;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class GlowshroomGlowshroomGlowshroomPoster extends HutThing{
    int frameType = 1;
    public GlowshroomGlowshroomGlowshroomPoster(){
        super("Glowhsroom Glowshroom Glowshroom Poster", "glowshroom poster", -1);
    }
    @Override
    public HutThing newInstance(){
        return new GlowshroomGlowshroomGlowshroomPoster();
    }
    @Override
    public void render(int width, int height){
        Core.applyColor(Color.white);
        Renderer2D.drawRect(0, 0, width, height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/special/glowshroom_poster/background.png"));
        Renderer2D.drawRect(0, 0, width, height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/special/glowshroom_poster/frame "+frameType+".png"));
    }
}