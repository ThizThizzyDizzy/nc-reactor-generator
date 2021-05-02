package discord.play.smivilization;
import simplelibrary.image.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.lwjgl.opengl.GL11;
import planner.Core;
import simplelibrary.config2.Config;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public abstract class HutThing implements Comparable<HutThing>{
    private final String texture;
    protected final Hut hut;
    private final String name;
    private final long price;
    public UUID parent;
    public int x,y,z;
    public final UUID uuid;
    protected int mirrorIf = 0;//if -1, mirror on left side. if +1, mirror on right side
    public Wall wall;
    protected HutThing(Hut hut, String name, String textureName, long price){
        this(UUID.randomUUID(), hut, name, textureName, price);
    }
    public HutThing(UUID uid, Hut hut, String name, String textureName, long price){
        this.uuid = uid;
        this.texture = "/textures/smivilization/buildings/huts/gliese/furniture/"+textureName+".png";
        this.hut = hut;
        this.name = name;
        this.price = price;
        int[] loc = getDefaultLocation();
        x = loc[0];
        y = loc[1];
        z = loc[2];
        wall = getDefaultWall();
    }
    public HutThing newInstance(Hut hut){
        return newInstance(UUID.randomUUID(), hut);
    }
    public abstract HutThing newInstance(UUID uuid, Hut hut);
    public String getTexture(){
        return texture;
    }
    public boolean isEqual(HutThing thing){
        return name.equals(thing.name);
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
        config.set("uuid", uuid.toString());
        config.set("x", x);
        config.set("y", y);
        config.set("z", z);
        config.set("wall", wall.ordinal());
        if(parent!=null)config.set("parent", parent.toString());
        return config;
    }
    public static HutThing load(Config config, Hut hut){
        String name = config.get("name");
        String texture = config.get("texture");
        HutThing thing = null;
        for(HutThing furn : Hut.allFurniture){
            if(furn.name.equals(name)||furn.texture.equals(texture))thing = furn.newInstance(UUID.fromString(config.get("uuid", UUID.randomUUID().toString())), hut);
        }
        if(config.hasProperty("x")){
            thing.x = config.get("x");
            thing.y = config.get("y");
            thing.z = config.get("z");
        }
        thing.wall = Wall.values()[config.get("wall", thing.getAllowedWalls()[0].ordinal())];
        if(config.hasProperty("parent"))thing.parent = UUID.fromString(config.get("parent"));
        if(thing==null)return null;
        thing.postLoad(config);
        return thing;
    }
    protected void postLoad(Config config){}
    public void render(double x, double y, float scale, float imgScale){
        Core.applyColor(Color.WHITE);
        boolean mirror = false;
        float center = this.x+getDimX()/2f;
        if(mirrorIf>0&&center>4.5f)mirror = true;
        if(mirrorIf<0&&center<4.5f)mirror = true;
        if(mirror)draw(x+(getRenderWidth()-getRenderOriginX())*scale*getRenderScale()*imgScale, y-getRenderOriginY()*scale*getRenderScale()*getRenderScaleY()*imgScale, x-getRenderOriginX()*scale*getRenderScale()*imgScale, y+(getRenderHeight()-getRenderOriginY())*scale*getRenderScale()*getRenderScaleY()*imgScale);
        else draw(x-getRenderOriginX()*scale*getRenderScale()*imgScale, y-getRenderOriginY()*scale*getRenderScale()*getRenderScaleY()*imgScale, x+(getRenderWidth()-getRenderOriginX())*scale*getRenderScale()*imgScale, y+(getRenderHeight()-getRenderOriginY())*scale*getRenderScale()*getRenderScaleY()*imgScale);
    }
    public void draw(double left, double top, double right, double bottom){
        Renderer2D.drawRect(left, top, right, bottom, ImageStash.instance.getTexture(getTexture()));
    }
    public abstract float getRenderWidth();
    public abstract float getRenderHeight();
    public abstract float getRenderOriginX();
    public abstract float getRenderOriginY();
    public float getRenderScale(){
        return 1;
    }
    public float getRenderScaleY(){
        return 1;
    }
    public abstract int[] getDefaultLocation();//used when buying stuff (if possible) or when converting huts
    public abstract Wall getDefaultWall();
    public abstract int[] getDimensions();
    public final int[] getDimensions(Wall wall){
        Wall actual = this.wall;
        this.wall = wall;
        int[] dims = getDimensions();
        this.wall = actual;
        return dims;
    }
    public final int getDimX(){
        return getDimensions()[0];
    }
    public final int getDimY(){
        return getDimensions()[1];
    }
    public final int getDimZ(){
        return getDimensions()[2];
    }
    public final int getDimX(Wall wall){
        return getDimensions(wall)[0];
    }
    public final int getDimY(Wall wall){
        return getDimensions(wall)[1];
    }
    public final int getDimZ(Wall wall){
        return getDimensions(wall)[2];
    }
    public abstract Wall[] getAllowedWalls();
    @Override
    public int compareTo(HutThing o){
        if(o.isBackgroundObject()&&!isBackgroundObject())return 1;
        if(isBackgroundObject()&&!o.isBackgroundObject())return -1;
        if(o.y==y)return z-o.z;
        return y-o.y;
    }
    public void getPlacementPoints(ArrayList<PlacementPoint> points){}
    protected void addHorizontalPlacementPointGrid(Wall wall, int xOff, int yOff, int z, int width, int depth, List<PlacementPoint> points){
        for(int y = 0; y<depth; y++){
            float Y = doSomeMagic(y/(float)depth)*depth;
            float dep = (Y/(float)(depth-1));
            if(Float.isNaN(dep))dep = 0;
            for(int x = 0; x<width; x++){
                float wid = (x/(float)(width-1));
                if(Float.isNaN(wid))wid = 0;
                points.add(new PlacementPoint(this, wall, x+xOff, y+yOff, z));
            }
        }
    }
    private float doSomeMagic(float f){
        return (float)Math.pow(f,1.25f);
    }
    public void render(float imgScale){
        GL11.glColor4d(1, 1, 1, 1);
        switch(wall){
            case FLOOR:
                float X = x+getDimX()/2;
                float Y = y+getDimY()/2;
                float Z = z;
                double[] xy = Hut.convertXYZtoXY512(X, Y, Z);
                float scale = hut.getScale(Y);
                render(xy[0], xy[1], scale, .25f);
                break;
            case CIELING:
                X = x+getDimX()/2;
                Y = y+getDimY()/2;
                Z = z+1;//cuz Z is only half-flipped
                xy = Hut.convertXYZtoXY512(X, Y, Z);
                scale = hut.getScale(Y);
                render(xy[0], xy[1], scale, .25f);
                break;
            case LEFT:
            case RIGHT:
                X = x;
                Y = y+getDimY()/2;
                Z = z+getDimZ()/2;
                xy = Hut.convertXYZtoXY512(X, Y, Z);
                scale = hut.getScale(Y);
                render(xy[0], xy[1], scale, .25f);
                break;
            default:
                throw new IllegalArgumentException("Cannot render on wall "+wall.toString()+"!");
        }
    }
    public boolean isBackgroundObject(){
        return false;
    }
    public boolean isLamp(){
        return false;
    }
    public boolean isLightSwitch(){
        return false;
    }
    public boolean isOn(){
        throw new IllegalArgumentException("This HutThing is a light switch or lamp, but doesn't know when it's on or not!");
    }
    public void setOn(boolean on){
        throw new IllegalArgumentException("This HutThing is a light switch or lamp, but doesn't know how to set if it's on or not!");
    }
}