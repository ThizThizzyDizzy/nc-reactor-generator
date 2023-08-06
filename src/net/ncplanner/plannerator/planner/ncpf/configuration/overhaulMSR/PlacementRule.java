package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
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
        if(!rule.hasSubRules){
            if(target.definition.typeMatches(NCPFModuleElement::new)){
                blockType = target.copyTo(NCPFModuleReference::new);
            }else block = target.copyTo(BlockReference::new);
        }
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        if(block==null)target = blockType;
        else target = block;
        super.convertToObject(ncpf);
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        if(lst==null){
            if(target==null)return;
            if(target.definition.typeMatches(NCPFModuleElement::new)){
                target = blockType = target.copyTo(NCPFModuleReference::new);
            }else target = block = target.copyTo(BlockReference::new);
        }else if(!rule.hasSubRules&&target!=null)target.setReferences(lst);//target should never be null
    }
}