package discord.play.smivilization;
import java.awt.Color;
import planner.Core;
import simplelibrary.config2.Config;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public abstract class HutThingColorable extends HutThing{
    private Color color;
    public HutThingColorable(String name, String textureName, long price, Color defaultColor){
        super(name, textureName, price);
        this.color = defaultColor;
    }
    public HutThingColorable setColor(Color color){
        this.color = color;
        return this;
    }
    public Color getColor(){
        return color;
    }
    @Override
    public Config save(Config config){
        config.set("rgb", color.getRGB());
        return super.save(config);
    }
    @Override
    protected void postLoad(Config config){
        color = new Color(config.get("rgb"));
    }
    @Override
    public void render(int width, int height){
        Core.applyColor(getColor());
        Renderer2D.drawRect(0, 0, width, height, ImageStash.instance.getTexture(getTexture()));
    }
}