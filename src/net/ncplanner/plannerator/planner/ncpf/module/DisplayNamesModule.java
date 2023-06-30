package net.ncplanner.plannerator.planner.ncpf.module;
import java.util.ArrayList;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class DisplayNamesModule extends NCPFModule{
    public String displayName;
    public ArrayList<String> legacyNames;
    public DisplayNamesModule(){
        super("plannerator:display_names");
    }
    @Override
    public void conglomerate(NCPFModule addon){}
    @Override
    public void convertFromObject(NCPFObject ncpf){
        displayName = ncpf.getString("display_name");
        NCPFList legacy = ncpf.getNCPFList("legacy_names");
        for(int i = 0; i<legacy.size(); i++)legacyNames.add(legacy.getString(i));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("display_name", displayName);
        NCPFList<String> legacy = new NCPFList<>();
        legacy.addAll(legacyNames);
        ncpf.setNCPFList("legacy_names", legacy);
    }
}