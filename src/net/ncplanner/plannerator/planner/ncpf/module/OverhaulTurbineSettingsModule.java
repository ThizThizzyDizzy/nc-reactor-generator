package net.ncplanner.plannerator.planner.ncpf.module;
public class OverhaulTurbineSettingsModule extends NCPFSettingsModule{
    public int minWidth;
    public int minLength;
    public int maxSize;
    public int fluidPerBlade;
    public float throughputFactor;
    public float powerBonus;
    public float throughputEfficiencyLeniencyMultiplier;
    public float throughputEfficiencyLeniencyThreshold;
    public OverhaulTurbineSettingsModule(){
        super("nuclearcraft:overhaul_turbine_configuration_settings");
        addInteger("min_width", ()->minWidth, (v)->minWidth = v, "Minimum Width", "The minimum width of this multiblock");
        addInteger("max_size", ()->maxSize, (v)->maxSize = v, "Maximum Size", "The maximum size of this multiblock");
        addFloat("throughput_efficiency_leniency_multiplier", ()->throughputEfficiencyLeniencyMultiplier, (v)->throughputEfficiencyLeniencyMultiplier = v, "Throughput Efficiency Leniency Mult");
        addFloat("throughput_factor", ()->throughputFactor, (v)->throughputFactor = v, "Throughput Factor");
        addInteger("min_length", ()->minLength, (v)->minLength = v, "Minimum Length", "The minimum length of this multiblock");
        addInteger("fluid_per_blade", ()->fluidPerBlade, (v)->fluidPerBlade = v, "Fluid Per Blade", "The maximum fluid input per blade");
        addFloat("throughput_efficiency_leniency_threshold", ()->throughputEfficiencyLeniencyThreshold, (v)->throughputEfficiencyLeniencyThreshold = v, "Throughput Efficiency Leniency Threshold");
        addFloat("power_bonus", ()->powerBonus, (v)->powerBonus = v, "Power Bonus");
    }
}