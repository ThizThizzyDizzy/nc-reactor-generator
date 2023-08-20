package net.ncplanner.plannerator.planner.ncpf.module;
import java.util.ArrayList;
import java.util.List;
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
    public String getFriendlyName(){
        return "Legacy Names";
    }
}