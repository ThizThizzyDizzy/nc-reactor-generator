package net.ncplanner.plannerator.discord.play.smivilization;
import java.util.UUID;
import net.ncplanner.plannerator.Renderer;
import simplelibrary.config2.Config;
import simplelibrary.image.Color;
public abstract class HutThingColorable extends HutThing{
    private Color color;
    public HutThingColorable(UUID uuid, Hut hut, String name, String textureName, long price, Color defaultColor){
        super(uuid, hut, name, textureName, price);
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
    public void draw(Renderer renderer, double left, double top, double right, double bottom){
        renderer.setColor(getColor());
        super.draw(renderer, left, top, right, bottom);
    }
    @Override
    public abstract int[] getDimensions();
}