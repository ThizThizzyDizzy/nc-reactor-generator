package net.ncplanner.plannerator.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
public class NCPFOverhaulMSRConfiguration extends NCPFConfiguration{
    public List<NCPFElement> blocks = new ArrayList<>();
    public NCPFOverhaulMSRConfiguration(){
        super("nuclearcraft:overhaul_msr");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        blocks = ncpf.getDefinedNCPFList("blocks", NCPFElement::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        super.convertToObject(ncpf);
        ncpf.setDefinedNCPFList("blocks", blocks);
    }
    @Override
    public void conglomerate(DefinedNCPFModularObject obj){
        super.conglomerate(obj);
        NCPFOverhaulMSRConfiguration addon = (NCPFOverhaulMSRConfiguration) obj;
        conglomerateElementList(blocks, addon.blocks);
    }
    @Override
    public List<NCPFElement>[] getElements(){
        return new List[]{blocks};
    }
    @Override
    public void makePartial(List<Design> designs){}
}