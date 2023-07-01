package net.ncplanner.plannerator.ncpf.configuration;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFOverhaulSFRConfiguration extends NCPFConfiguration{
    public List<NCPFElement> blocks;
    public List<NCPFElement> coolantRecipes;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        blocks = ncpf.getDefinedNCPFList("blocks", blocks, NCPFElement::new);
        coolantRecipes = ncpf.getDefinedNCPFList("coolant_recipes", coolantRecipes, NCPFElement::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        super.convertToObject(ncpf);
        ncpf.setDefinedNCPFList("blocks", blocks);
        ncpf.setDefinedNCPFList("coolant_recipes", coolantRecipes);
    }
    @Override
    public void conglomerate(DefinedNCPFModularObject obj){
        super.conglomerate(obj);
        NCPFOverhaulSFRConfiguration addon = (NCPFOverhaulSFRConfiguration) obj;
        conglomerateElementList(blocks, addon.blocks);
        conglomerateElementList(coolantRecipes, addon.coolantRecipes);
    }
}