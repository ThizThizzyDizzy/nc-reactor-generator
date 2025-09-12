package net.ncplanner.plannerator.planner.ncpf.module.configuration.settings;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class OverhaulDistillerSettingsModule extends NCPFSettingsModule{
    public int minSize;
    public int maxSize;
    public int baseTime;
    public int basePower;
    public OverhaulDistillerSettingsModule(){
        super("nuclearcraft:overhaul_distiller_configuration_settings");
        addInteger("min_size", () -> minSize, (v) -> minSize = v, "Minimum Size", "The minimum size of this multiblock");
        addInteger("max_size", () -> maxSize, (v) -> maxSize = v, "Maximum Size", "The maximum size of this multiblock");
        addInteger("base_time", () -> baseTime, (v) -> baseTime = v, "Base Time", "Base ticks per distiller process.");
        addInteger("base_power", () -> basePower, (v) -> basePower = v, "Base Power", "Base RF/t use during distilling");
    }

}
