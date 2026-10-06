package net.ncplanner.plannerator.planner.configuration;
import java.util.ArrayList;
import net.ncplanner.ncpf.io.NcpfJsonConverter;
import net.ncplanner.ncpf.structure.NcpfAddon;
import net.ncplanner.ncpf.structure.NcpfConfigurations;
import net.ncplanner.ncpf.structure.NcpfRoot;
public class CannedConfiguration{
    public CannedConfiguration(){
        this.configuration = new NcpfConfigurations();
        this.path = null;
    }
    public CannedConfiguration(NcpfRoot ncpf){
        this(ncpf, null);
    }
    public CannedConfiguration(NcpfRoot ncpf, String path){
        configuration = ncpf.configuration;
        addons.addAll(ncpf.addons);
        this.path = path;
    }
    public NcpfConfigurations configuration;
    public final ArrayList<NcpfAddon> addons = new ArrayList();
    public String path;
    public ArrayList<String> aliases = new ArrayList<>();
    public CannedConfiguration safeCopy(){
        // convert to JSON and back, to create all new fresh instances...
        return NcpfJsonConverter.gson.fromJson(NcpfJsonConverter.gson.toJson(this), CannedConfiguration.class);
    }
    public void impose(NcpfRoot project){
        project.configuration = configuration;
        
        project.addons = addons;
        
        //TODO recalculate any cached references?
    }
    public CannedConfiguration addAlias(String alias){
        aliases.add(alias);
        return this;
    }
}
