package discord.play.smivilization;
import java.util.ArrayList;
import simplelibrary.config2.Config;
public abstract class HutThing{
    private final String texture;
    private final String name;
    private final long price;
    public final ArrayList<HutThing> requires = new ArrayList<>();
    public HutThing(String name, String textureName, long price){
        this.texture = "/textures/smivilization/buildings/huts/gliese/"+textureName+".png";
        this.name = name;
        this.price = price;
    }
    public int getLayer(){
        return Hut.allFurniture.indexOf(this);
    }
    public abstract HutThing newInstance();
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
    protected final void require(String thin){
        for(HutThing thing : Hut.allFurniture){
            if(thing.name.equalsIgnoreCase(thin))requires.add(thing);
        }
    }
    public boolean isSellable(){
        return price>-1;
    }
    public Config save(Config config){
        config.set("name", name);
        config.set("texture", texture);
        return config;
    }
    public static HutThing load(Config config){
        String name = config.get("name");
        String texture = config.get("texture");
        HutThing thing = null;
        for(HutThing furn : Hut.allFurniture){
            if(furn.name.equals(name)||furn.texture.equals(texture))thing = furn.newInstance();
        }
        if(thing==null)return null;
        thing.postLoad(config);
        return thing;
    }
    protected void postLoad(Config config){}
}