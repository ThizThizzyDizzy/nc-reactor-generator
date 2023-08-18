package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class DisplayNameModule extends NCPFModule{
    public String displayName;
    public DisplayNameModule(){
        super("plannerator:display_name");
    }
    @Override
    public void conglomerate(NCPFModule addon){}
    @Override
    public void convertFromObject(NCPFObject ncpf){
        displayName = ncpf.getString("display_name");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("display_name", displayName);
    }
}