package net.ncplanner.plannerator.multiblock.tinkers;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.SimpleBlock;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
public class ToolPart extends SimpleBlock{
    public PartType type;
    public PartMaterial material;
    public ToolPart(Configuration configuration, int x, int y, int z, PartType type, PartMaterial material){
        super(configuration, x, y, z);
        this.type = type;
        this.material = material;
    }
    @Override
    public String getListTooltip(){
        return getName()+material.getTooltips(type);
    }
    @Override
    public AbstractBlock newInstance(int x, int y, int z){
        return new ToolPart(configuration, x, y, z, type, material);
    }
    @Override
    public String getName(){
        return material.toString()+" "+type.toString();
    }
}