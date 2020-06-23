package planner.multiblock.underhaul.fissionsfr;
import java.awt.image.BufferedImage;
public class Block implements planner.multiblock.Block{
    private final planner.configuration.underhaul.fissionsfr.Block template;
    public Block(planner.configuration.underhaul.fissionsfr.Block template){
        this.template = template;
    }
    @Override
    public planner.multiblock.Block newInstance(){
        return new Block(template);
    }
    @Override
    public BufferedImage getTexture(){
        return template.texture;
    }
    @Override
    public String getName(){
        return template.name;
    }
}