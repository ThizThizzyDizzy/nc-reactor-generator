package net.ncplanner.plannerator.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
public class NCPFOverhaulSFRConfiguration extends NCPFConfiguration{
    public List<NCPFElement> blocks = new ArrayList<>();
    public List<NCPFElement> coolantRecipes = new ArrayList<>();
    public NCPFOverhaulSFRConfiguration(){
        super("nuclearcraft:overhaul_sfr");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        blocks = ncpf.getDefinedNCPFList("blocks", NCPFElement::new);
        coolantRecipes = ncpf.getDefinedNCPFList("coolant_recipes", NCPFElement::new);
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
    @Override
    public List<NCPFElement>[] getElements(){
        return new List[]{blocks, coolantRecipes};
    }
    @Override
    public void makePartial(List<Design> designs){}
}