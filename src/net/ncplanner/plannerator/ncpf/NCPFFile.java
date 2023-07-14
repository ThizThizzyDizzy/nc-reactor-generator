package net.ncplanner.plannerator.ncpf;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import java.util.ArrayList;
public class NCPFFile extends DefinedNCPFModularConfigurationContainer{
    public int version;
    public ArrayList<NCPFAddon> addons = new ArrayList<>();
    public ArrayList<NCPFDesign> designs = new ArrayList<>();
    public NCPFConfigurationContainer conglomeration = new NCPFConfigurationContainer();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        version = ncpf.getInteger("version");
        addons = ncpf.getDefinedNCPFList("addons", new NCPFList<>(), NCPFAddon::new);
        conglomerate();
        designs = ncpf.getDefinedNCPFList("designs", new NCPFList<>(), ()->{return new NCPFDesign(this);});
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("version", version);
        ncpf.setDefinedNCPFList("addons", addons);
        ncpf.setDefinedNCPFList("designs", designs);
        super.convertToObject(ncpf);
    }
    public void conglomerate(){
        conglomeration = new NCPFConfigurationContainer();
        conglomeration.conglomerate(configuration);
        for(NCPFAddon addon : addons){
            conglomeration.conglomerate(addon.configuration);
        }
        conglomeration.setReferences();
    }
}