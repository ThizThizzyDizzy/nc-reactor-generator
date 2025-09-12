package net.ncplanner.plannerator.planner.ncpf.module;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class TagsModule extends NCPFSettingsModule implements ElementModule{
    public List<String> tags = new ArrayList<>();
    public TagsModule(){
        super("plannerator:tags");
        addStringList("tags", () -> tags, (v) -> tags = v, "Oredict/Tags");
    }
    @Override
    public void conglomerate(NCPFModule addon){
    }
    @Override
    public String getFriendlyName(){
        return "Oredict/Tags";
    }
}
