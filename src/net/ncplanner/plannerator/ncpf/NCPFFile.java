package net.ncplanner.plannerator.ncpf;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import java.util.ArrayList;
public class NCPFFile extends DefinedNCPFObject{
    public int version;
    public NCPFConfigurationContainer configuration;
    public ArrayList<NCPFAddon> addons;
    public ArrayList<NCPFDesign> designs;
    public NCPFConfigurationContainer conglomeration;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        version = ncpf.getInteger("version");
        configuration = ncpf.getDefinedNCPFObject("configuration", NCPFConfigurationContainer::new);
        addons = ncpf.getDefinedNCPFList("addons", new NCPFList<>(), NCPFAddon::new);
        conglomeration = new NCPFConfigurationContainer();
        conglomeration.conglomerate(configuration);
        for(NCPFAddon addon : addons){
            conglomeration.conglomerate(addon.configuration);
        }
        designs = ncpf.getDefinedNCPFList("designs", new NCPFList<>(), ()->{return new NCPFDesign(this);});
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("version", version);
        ncpf.setDefinedNCPFObject("configuration", configuration);
        ncpf.setDefinedNCPFList("addons", addons);
        ncpf.setDefinedNCPFList("designs", designs);
    }
}