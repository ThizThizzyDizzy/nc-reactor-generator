package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulDistillerConfiguration;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.DistillerRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.settings.OverhaulDistillerSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class OverhaulDistillerConfiguration extends NCPFOverhaulDistillerConfiguration{
    public ConfigurationMetadataModule metadata = new ConfigurationMetadataModule();
    public OverhaulDistillerSettingsModule settings;
    public List<BlockElement> blocks = new ArrayList<>();
    public List<DistillerRecipe> recipes = new ArrayList<>();
    public OverhaulDistillerConfiguration(){
        definePlanneratorModule(true, () -> metadata, (m) -> metadata = m, ConfigurationMetadataModule::new);
        definePlanneratorModule(false, () -> settings, (m) -> settings = m, OverhaulDistillerSettingsModule::new);
        definePlanneratorField(super.getClass(), getClass(), "blocks", (c) -> c.blocks, (lst) -> blocks = lst, BlockElement::new);
        definePlanneratorField(super.getClass(), getClass(), "recipes", (c) -> c.recipes, (lst) -> recipes = lst, DistillerRecipe::new);
    }
    public int boundSize(int size){
        return Math.max(settings.minSize, Math.min(settings.maxSize, size));
    }
}
