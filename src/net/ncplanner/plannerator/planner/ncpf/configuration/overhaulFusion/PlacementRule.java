package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion;
import net.ncplanner.plannerator.ncpf.NCPFModuleReference;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class PlacementRule extends NCPFPlacementRule{
    public BlockReference block;
    public NCPFModuleReference blockType;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        if(super.target.definition.typeMatches(NCPFModuleElement::new)){
            blockType = super.target.copyTo(NCPFModuleReference::new);
        }else block = super.target.copyTo(BlockReference::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        if(block==null)super.target = blockType;
        else super.target = block;
        super.convertToObject(ncpf);
    }
}