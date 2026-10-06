package net.ncplanner.plannerator.discord.play.smivilization;
import com.thizthizzydizzy.dizzyengine.graphics.Renderer;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import java.util.UUID;
import net.ncplanner.plannerator.config2.Config;
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
    public void draw(float left, float top, float right, float bottom){
        Renderer.setColor(getColor());
        super.draw(left, top, right, bottom);
    }
    @Override
    public abstract int[] getDimensions();
}