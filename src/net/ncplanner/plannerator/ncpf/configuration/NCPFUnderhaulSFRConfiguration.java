package net.ncplanner.plannerator.ncpf.configuration;
import java.util.ArrayList;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFUnderhaulSFRConfiguration extends NCPFConfiguration{
    public ArrayList<NCPFElement> blocks;
    public ArrayList<NCPFElement> fuels;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        blocks = ncpf.getDefinedNCPFList("blocks", blocks, NCPFElement::new);
        fuels = ncpf.getDefinedNCPFList("fuels", fuels, NCPFElement::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        super.convertToObject(ncpf);
        ncpf.setDefinedNCPFList("blocks", blocks);
        ncpf.setDefinedNCPFList("fuels", fuels);
    }
    @Override
    public void conglomerate(DefinedNCPFModularObject obj){
        super.conglomerate(obj);
        NCPFUnderhaulSFRConfiguration addon = (NCPFUnderhaulSFRConfiguration) obj;
        conglomerateElementList(blocks, addon.blocks);
        conglomerateElementList(fuels, addon.fuels);
    }
}