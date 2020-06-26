package planner.multiblock.overhaul.fissionsfr;
import java.awt.image.BufferedImage;
public class Block extends planner.multiblock.Block{
    private final planner.configuration.overhaul.fissionsfr.Block template;
    public Block(int x, int y, int z, planner.configuration.overhaul.fissionsfr.Block template){
        super(x, y, z);
        this.template = template;
    }
    @Override
    public planner.multiblock.Block newInstance(int x, int y, int z){
        return new Block(x, y, z, template);
    }
    @Override
    public BufferedImage getBaseTexture(){
        return template.texture;
    }
    @Override
    public BufferedImage getTexture(){
        return template.displayTexture;
    }
    @Override
    public String getName(){
        return template.name;
    }
    @Override
    public void clearData(){
    }
    @Override
    public String getTooltip(){
        return "";
    }
    @Override
    public boolean isActive(){
        return false;
    }
}