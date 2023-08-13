package net.ncplanner.plannerator.multiblock.tinkers;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.SimpleBlock;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.UnknownNCPFElement;
public class ToolPart extends SimpleBlock{
    public PartType type;
    public PartMaterial material;
    public ToolPart(NCPFConfigurationContainer configuration, int x, int y, int z, PartType type, PartMaterial material){
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
    @Override
    public NCPFElement getTemplate(){
        return new NCPFElement(new UnknownNCPFElement());
    }
}