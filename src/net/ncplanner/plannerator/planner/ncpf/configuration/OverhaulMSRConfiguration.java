package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.settings.OverhaulMSRSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class OverhaulMSRConfiguration extends NCPFOverhaulMSRConfiguration{
    public ConfigurationMetadataModule metadata = new ConfigurationMetadataModule();
    public OverhaulMSRSettingsModule settings;
    public List<BlockElement> blocks = new ArrayList<>();
    public OverhaulMSRConfiguration(){
        definePlanneratorModule(true, () -> metadata, (m) -> metadata = m, ConfigurationMetadataModule::new);
        definePlanneratorModule(false, () -> settings, (m) -> settings = m, OverhaulMSRSettingsModule::new);
        definePlanneratorField(super.getClass(), getClass(), "blocks", (c) -> c.blocks, (lst) -> blocks = lst, BlockElement::new);
    }
    public int boundSize(int size){
        return Math.max(settings.minSize, Math.min(settings.maxSize, size));
    }
}
