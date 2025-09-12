package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.TurbineRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.settings.OverhaulTurbineSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class OverhaulTurbineConfiguration extends NCPFOverhaulTurbineConfiguration{
    public ConfigurationMetadataModule metadata = new ConfigurationMetadataModule();
    public OverhaulTurbineSettingsModule settings;
    public List<BlockElement> blocks = new ArrayList<>();
    public List<TurbineRecipe> recipes = new ArrayList<>();
    public OverhaulTurbineConfiguration(){
        definePlanneratorModule(true, () -> metadata, (m) -> metadata = m, ConfigurationMetadataModule::new);
        definePlanneratorModule(false, () -> settings, (m) -> settings = m, OverhaulTurbineSettingsModule::new);
        definePlanneratorField(super.getClass(), getClass(), "blocks", (c) -> c.blocks, (lst) -> blocks = lst, BlockElement::new);
        definePlanneratorField(super.getClass(), getClass(), "recipes", (c) -> c.recipes, (lst) -> recipes = lst, TurbineRecipe::new);
    }
}
