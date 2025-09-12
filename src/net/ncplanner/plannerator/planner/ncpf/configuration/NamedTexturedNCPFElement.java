package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
public abstract class NamedTexturedNCPFElement extends NCPFElement{
    public DisplayNameModule names = new DisplayNameModule();
    public TextureModule texture = new TextureModule();
    public NamedTexturedNCPFElement(){
        definePlanneratorModule(()->names, (m)->names = m, DisplayNameModule::new);
        definePlanneratorModule(()->texture, (m)->texture = m, TextureModule::new);
    }
    @Override
    public Supplier<NCPFModule>[] getPreferredModules(){
        List<Supplier<NCPFModule>> list = new ArrayList<>();
        definedPlanneratorModules.forEach((module) -> {
            if(module.get.get()==names||module.get.get()==texture)return;
            list.add(module.moduleSupplier);
        });
        return list.toArray(Supplier[]::new);
    }
}
