package multiblock.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.Objects;
import multiblock.configuration.TextureManager;
import planner.Core;
import planner.Pinnable;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
import simplelibrary.image.Image;
public class Fuel implements Pinnable{
    public static Fuel fuel(String name, String displayName, float power, float heat, int time, String texture){
        Fuel fuel = new Fuel(name, power, heat, time);
        fuel.displayName = displayName;
        fuel.legacyNames.add(displayName);
        fuel.setTexture(TextureManager.getImage(texture));
        return fuel;
    }
    public String name;
    public String displayName;
    public ArrayList<String> legacyNames = new ArrayList<>();
    public float power;
    public float heat;
    public int time;
    public Image texture;
    public Image displayTexture;
    public Fuel(String name, float power, float heat, int time){
        this.name = name;
        this.power = power;
        this.heat = heat;
        this.time = time;
    }
    public Config save(boolean partial){
        Config config = Config.newConfig();
        config.set("name", name);
        if(!partial){
            if(displayName!=null)config.set("displayName", displayName);
            if(!legacyNames.isEmpty()){
                ConfigList lst = new ConfigList();
                for(String s : legacyNames)lst.add(s);
                config.set("legacyNames", lst);
            }
        }
        config.set("power", power);
        config.set("heat", heat);
        config.set("time", time);
        if(!partial&&texture!=null){
            ConfigNumberList tex = new ConfigNumberList();
            tex.add(texture.getWidth());
            for(int x = 0; x<texture.getWidth(); x++){
                for(int y = 0; y<texture.getHeight(); y++){
                    tex.add(texture.getRGB(x, y));
                }
            }
            config.set("texture", tex);
        }
        return config;
    }
    public void setTexture(Image image){
        texture = image;
        displayTexture = TextureManager.convert(image);
    }
    public Image getTexture(){
        return texture;
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof Fuel){
            Fuel f = (Fuel)obj;
            return Objects.equals(name, f.name)
                    &&Objects.equals(displayName, f.displayName)
                    &&legacyNames.equals(f.legacyNames)
                    &&power==f.power
                    &&heat==f.heat
                    &&time==f.time
                    &&Core.areImagesEqual(texture, f.texture);
        }
        return super.equals(obj);
    }
    public ArrayList<String> getLegacyNames(){
        ArrayList<String> allNames = new ArrayList<>(legacyNames);
        allNames.add(name);
        return allNames;
    }
    public String getDisplayName(){
        return displayName==null?name:displayName;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> lst = getLegacyNames();
        lst.add(getDisplayName());
        return lst;
    }
    @Override
    public String getPinnedName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }
    public void setLegacyNames(ArrayList<String> legacyNames){
        this.legacyNames = new ArrayList<>(legacyNames);
    }
    public float getPower(){
        return power;
    }
    public float getHeat(){
        return heat;
    }
    public int getTime(){
        return time;
    }
    public void setPower(float power){
        this.power = power;
    }
    public void setHeat(float heat){
        this.heat = heat;
    }
    public void setTime(int time){
        this.time = time;
    }
}