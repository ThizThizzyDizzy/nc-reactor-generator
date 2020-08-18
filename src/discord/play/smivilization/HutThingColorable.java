package discord.play.smivilization;
import java.awt.Color;
import java.util.UUID;
import planner.Core;
import simplelibrary.config2.Config;
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
    public void draw(double left, double top, double right, double bottom){
        Core.applyColor(getColor());
        super.draw(left, top, right, bottom);
    }
    @Override
    public abstract int[] getDimensions();
}