package net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.Objects;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockTemplate;
import net.ncplanner.plannerator.multiblock.configuration.RuleContainer;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.file.writer.LegacyNCPFWriter;
public class Block extends RuleContainer<PlacementRule.BlockType, Block> implements Pinnable, IBlockTemplate {
    public String name;
    public String displayName;
    public ArrayList<String> legacyNames = new ArrayList<>();
    public int cooling = 0;
    public boolean fuelCell = false;
    public boolean moderator = false;
    public boolean casing = false;
    public boolean controller = false;
    public String active;
    public Image texture;
    public Image displayTexture;
    public Block(String name){
        this.name = name;
    }
    public void setTexture(Image image){
        texture = image;
        Image displayImg = new Image(image.getWidth(), image.getHeight());
        int left = Math.max(0,image.getWidth()/16-1);
        int right = Math.min(image.getWidth()*15/16, image.getWidth()-1);
        int up = Math.max(0,image.getHeight()/16-1);
        int down = Math.min(image.getHeight()*15/16, image.getHeight()-1);
        for(int x = 0; x<image.getWidth(); x++){
            for(int y = 0; y<image.getHeight(); y++){
                Color col = new Color(image.getRGB(x, y));
                if(active!=null){
                    if(x<=left||y<=up||x>=right||y>=down){
                        col = new Color(144, 238, 144);
                    }
                }
                displayImg.setRGB(x, y, TextureManager.convert(col).getRGB());
            }
        }
        displayTexture = displayImg;
    }
    @Override
    public boolean stillEquals(RuleContainer obj){
        if(obj!=null&&obj instanceof Block){
            Block b = (Block)obj;
            return Objects.equals(name, b.name)
                    &&Objects.equals(displayName, b.displayName)
                    &&legacyNames.equals(b.legacyNames)
                    &&cooling==b.cooling
                    &&fuelCell==b.fuelCell
                    &&moderator==b.moderator
                    &&casing==b.casing
                    &&controller==b.controller
                    &&Core.areImagesEqual(texture, b.texture)
                    &&Objects.equals(active, b.active);
        }
        return false;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String getDisplayName(){
        return displayName==null?name:displayName;
    }
    @Override
    public ArrayList<String> getLegacyNames(){
        ArrayList<String> allNames = new ArrayList<>(legacyNames);
        allNames.add(name);
        return allNames;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> nams = getSimpleSearchableNames();
        for(AbstractPlacementRule<PlacementRule.BlockType, Block> r : rules)nams.addAll(r.getSearchableNames());
        return nams;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        ArrayList<String> nams = getLegacyNames();
        nams.add(getDisplayName());
        return nams;
    }
    public Image getTexture(){
        return texture;
    }
    @Override
    public Image getDisplayTexture() {
        return displayTexture;
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
    public boolean isFuelCell(){
        return fuelCell;
    }
    public boolean isModerator(){
        return moderator;
    }
    public boolean isCasing(){
        return casing;
    }
    public boolean isController(){
        return controller;
    }
    public int getCooling(){
        return cooling;
    }
    public void setFuelCell(boolean fuelCell){
        this.fuelCell = fuelCell;
    }
    public void setModerator(boolean moderator){
        this.moderator = moderator;
    }
    public void setCasing(boolean casing){
        this.casing = casing;
    }
    public void setController(boolean controller){
        this.controller = controller;
    }
    public void setCooling(int cooling){
        this.cooling = cooling;
    }
    public String getActive(){
        return active;
    }
    public void setActive(String active){
        this.active = active;
    }
    @Override
    public String toString(){
        return getDisplayName();
    }
}