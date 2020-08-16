package discord.play.smivilization.thing.special;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import java.awt.Color;
import java.util.UUID;
import planner.Core;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class GlowshroomGlowshroomGlowshroomPoster extends HutThingExclusive{
    int frameType = 1;
    public GlowshroomGlowshroomGlowshroomPoster(UUID uuid, Hut hut){
        super(uuid, hut, "Glowhsroom Glowshroom Glowshroom Poster", "glowshroom poster", 210340018198151170l, -1);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new GlowshroomGlowshroomGlowshroomPoster(uuid, hut);
    }
    @Override
    public void render(int width, int height){
        Core.applyColor(Color.white);
        Renderer2D.drawRect(0, 0, width, height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/special/glowshroom_poster/background.png"));
        Renderer2D.drawRect(0, 0, width, height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/furniture/special/glowshroom_poster/frame "+frameType+".png"));
    }
}