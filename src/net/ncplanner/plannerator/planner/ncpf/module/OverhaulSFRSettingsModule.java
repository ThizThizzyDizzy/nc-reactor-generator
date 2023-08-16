package net.ncplanner.plannerator.planner.ncpf.module;
public class OverhaulSFRSettingsModule extends NCPFSettingsModule{
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public int coolingEfficiencyLeniency;
    public float sparsityPenaltyMultiplier;
    public float sparsityPenaltyThreshold;
    public OverhaulSFRSettingsModule(){
        super("nuclearcraft:overhaul_sfr_configuration_settings");
        addInteger("min_size", ()->minSize, (v)->minSize = v, "Minimum Size", "The minimum size of this multiblock");
        addFloat("sparsity_penalty_multiplier", ()->sparsityPenaltyMultiplier, (v)->sparsityPenaltyMultiplier = v, "Sparsity Penalty Multiplier");
        addInteger("neutron_reach", ()->neutronReach, (v)->neutronReach = v, "Neutron Reach", "The maximum length of moderator lines");
        addInteger("max_size", ()->maxSize, (v)->maxSize = v, "Maximum Size", "The maximum size of this multiblock");
        addFloat("sparsity_penalty_threshold", ()->sparsityPenaltyThreshold, (v)->sparsityPenaltyThreshold = v, "Sparsity Penalty Threshold");
        addInteger("cooling_efficiency_leniency", ()->coolingEfficiencyLeniency, (v)->coolingEfficiencyLeniency = v, "Cooling Efficiency Leniency", "The size of the \"safe zone\" around 0 H/t before you get overheating and overcooling penalties");
    }
}