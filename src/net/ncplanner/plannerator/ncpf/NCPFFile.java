package net.ncplanner.plannerator.ncpf;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.planner.Core;
public class NCPFFile extends DefinedNCPFModularConfigurationContainer{
    public int version;
    public List<NCPFAddon> addons = new ArrayList<>();
    public List<NCPFDesign> designs = new ArrayList<>();
    public NCPFConfigurationContainer conglomeration = new NCPFConfigurationContainer();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        version = ncpf.getInteger("version");
        addons = ncpf.getDefinedNCPFList("addons", NCPFAddon::new);
        postConvertFromObject(ncpf);
    }
    public void postConvertFromObject(NCPFObject ncpf){
        conglomerate();
        designs = ncpf.getDefinedNCPFList("designs", ()->{return new NCPFDesign(isConfigEmpty()?Core.project:this);});
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("version", version);
        ncpf.setDefinedNCPFList("addons", addons);
        super.convertToObject(ncpf);
        ncpf.setDefinedNCPFList("designs", designs);
    }
    public void conglomerate(){
        conglomeration = new NCPFConfigurationContainer();
        conglomeration.conglomerate(configuration);
        for(NCPFAddon addon : addons){
            conglomeration.conglomerate(addon.configuration);
        }
        conglomeration.setReferences();
    }
    public boolean isConfigEmpty(){
        return conglomeration.configurations.isEmpty();
    }
}