package discord.play.smivilization;
import java.awt.Color;
import simplelibrary.config2.Config;
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
}