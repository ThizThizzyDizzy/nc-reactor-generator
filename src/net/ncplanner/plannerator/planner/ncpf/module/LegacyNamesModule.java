package net.ncplanner.plannerator.planner.ncpf.module;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class LegacyNamesModule extends NCPFSettingsModule implements ElementModule{
    public List<String> legacyNames = new ArrayList<>();
    public LegacyNamesModule(){
        super("plannerator:legacy_names");
        addStringList("legacy_names", ()->legacyNames, (v)->legacyNames = v, "Legacy Names");
    }
    @Override
    public void conglomerate(NCPFModule addon){}
    @Override
    public void convertFromObject(NCPFObject ncpf){
        NCPFList legacy = ncpf.getNCPFList("legacy_names");
        for(int i = 0; i<legacy.size(); i++)legacyNames.add(legacy.getString(i));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        NCPFList<String> legacy = new NCPFList<>();
        legacy.addAll(legacyNames);
        ncpf.setNCPFList("legacy_names", legacy);
    }
    @Override
    public String getFriendlyName(){
        return "Legacy Names";
    }
}