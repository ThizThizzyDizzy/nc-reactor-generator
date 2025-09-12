package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.settings.OverhaulSFRSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class OverhaulSFRConfiguration extends NCPFOverhaulSFRConfiguration{
    public ConfigurationMetadataModule metadata = new ConfigurationMetadataModule();
    public OverhaulSFRSettingsModule settings;
    public List<BlockElement> blocks = new ArrayList<>();
    public List<CoolantRecipe> coolantRecipes = new ArrayList<>();
    public OverhaulSFRConfiguration(){
        definePlanneratorModule(true, () -> metadata, (m) -> metadata = m, ConfigurationMetadataModule::new);
        definePlanneratorModule(false, () -> settings, (m) -> settings = m, OverhaulSFRSettingsModule::new);
        definePlanneratorField(super.getClass(), getClass(), "blocks", (c) -> c.blocks, (lst) -> blocks = lst, BlockElement::new);
        definePlanneratorField(super.getClass(), getClass(), "coolant_recipes", (c) -> c.coolantRecipes, (lst) -> coolantRecipes = lst, CoolantRecipe::new);
    }
    public int boundSize(int size){
        return Math.max(settings.minSize, Math.min(settings.maxSize, size));
    }
}
