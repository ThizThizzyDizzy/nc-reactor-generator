package net.ncplanner.plannerator.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
public class NCPFOverhaulTurbineConfiguration extends NCPFConfiguration{
    public List<NCPFElement> blocks = new ArrayList<>();
    public List<NCPFElement> recipes = new ArrayList<>();
    public NCPFOverhaulTurbineConfiguration(){
        super("nuclearcraft:overhaul_turbine");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        blocks = ncpf.getDefinedNCPFList("blocks", NCPFElement::new);
        recipes = ncpf.getDefinedNCPFList("recipes", NCPFElement::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        super.convertToObject(ncpf);
        ncpf.setDefinedNCPFList("blocks", blocks);
        ncpf.setDefinedNCPFList("recipes", recipes);
    }
    @Override
    public void conglomerate(DefinedNCPFModularObject obj){
        super.conglomerate(obj);
        NCPFOverhaulTurbineConfiguration addon = (NCPFOverhaulTurbineConfiguration) obj;
        conglomerateElementList(blocks, addon.blocks);
        conglomerateElementList(recipes, addon.recipes);
    }
    @Override
    public List<NCPFElement>[] getElements(){
        return new List[]{blocks, recipes};
    }
    @Override
    public void makePartial(List<Design> designs){}
    @Override
    public String getName(){
        return "Overhaul Turbine Configuration";
    }
}