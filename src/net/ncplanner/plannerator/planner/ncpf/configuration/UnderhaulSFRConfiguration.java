package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.configuration.NCPFUnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.module.UnderhaulModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.settings.UnderhaulSFRSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = UnderhaulModule.class)
public class UnderhaulSFRConfiguration extends NCPFUnderhaulSFRConfiguration{
    public ConfigurationMetadataModule metadata = new ConfigurationMetadataModule();
    public UnderhaulSFRSettingsModule settings;
    public List<BlockElement> blocks = new ArrayList<>();
    public List<Fuel> fuels = new ArrayList<>();
    public UnderhaulSFRConfiguration(){
        definePlanneratorModule(true, () -> metadata, (m) -> metadata = m, ConfigurationMetadataModule::new);
        definePlanneratorModule(false, () -> settings, (m) -> settings = m, UnderhaulSFRSettingsModule::new);
        definePlanneratorField(super.getClass(), getClass(), "blocks", (c) -> c.blocks, (lst) -> blocks = lst, BlockElement::new);
        definePlanneratorField(super.getClass(), getClass(), "fuels", (c) -> c.fuels, (lst) -> fuels = lst, Fuel::new);
    }
    public int boundSize(int size){
        return Math.max(settings.minSize, Math.min(settings.maxSize, size));
    }
}
