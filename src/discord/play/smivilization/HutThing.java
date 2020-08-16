package discord.play.smivilization;
import java.awt.Color;
import java.util.UUID;
import planner.Core;
import simplelibrary.config2.Config;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public abstract class HutThing{
    private final String texture;
    protected final Hut hut;
    private final String name;
    private final long price;
    public UUID parent;
    public int x,y,z;
    public final UUID uuid;
    protected HutThing(Hut hut, String name, String textureName, long price){
        this(UUID.randomUUID(), hut, name, textureName, price);
    }
    public HutThing(UUID uid, Hut hut, String name, String textureName, long price){
        this.uuid = uid;
        this.texture = "/textures/smivilization/buildings/huts/gliese/furniture/"+textureName+".png";
        this.hut = hut;
        this.name = name;
        this.price = price;
    }
    public abstract HutThing newInstance(UUID uuid, Hut hut);
    public String getTexture(){
        return texture;
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof HutThing){
            return name.equals(((HutThing)obj).name);
        }
        return false;
    }
    public long getPrice(){
        return price;
    }
    public String getName(){
        return name;
    }
    public boolean isSellable(){
        return price>-1;
    }
    public Config save(Config config){
        config.set("name", name);
        config.set("texture", texture);
        config.set("uuid", uuid);
        config.set("x", x);
        config.set("y", y);
        config.set("z", z);
        config.set("parent", parent);
        return config;
    }
    public static HutThing load(Config config, Hut hut){
        String name = config.get("name");
        String texture = config.get("texture");
        HutThing thing = null;
        for(HutThing furn : Hut.allFurniture){
            if(furn.name.equals(name)||furn.texture.equals(texture))thing = furn.newInstance(config.get("uuid"), hut);
        }
        thing.x = config.get("x");
        thing.y = config.get("y");
        thing.z = config.get("z");
        thing.parent = config.get("parent");
        if(thing==null)return null;
        thing.postLoad(config);
        return thing;
    }
    protected void postLoad(Config config){}
    public void render(int width, int height){
        Core.applyColor(Color.white);
        Renderer2D.drawRect(0, 0, width, height, ImageStash.instance.getTexture(getTexture()));
    }
}