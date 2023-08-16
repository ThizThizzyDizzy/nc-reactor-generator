package net.ncplanner.plannerator.planner.ncpf.module;
public class OverhaulFusionSettingsModule extends NCPFSettingsModule{
    public int minInnerRadius;
    public int maxInnerRadius;
    public int minCoreSize;
    public int maxCoreSize;
    public int minToroidWidth;
    public int maxToroidWidth;
    public int minLiningThickness;
    public int maxLiningThickness;
    public float sparsityPenaltyMultiplier;
    public float sparsityPenaltyThreshold;
    public int coolingEfficiencyLeniency;
    public OverhaulFusionSettingsModule(){
        super("plannerator:fusion_test_configuration_settings");
        addInteger("min_inner_radius", ()->minInnerRadius, (v)->minInnerRadius = v, "Minimum Inner Radius");
        addInteger("min_core_size", ()->minCoreSize, (v)->minCoreSize = v, "Minimum Core Size");
        addInteger("min_toroid_width", ()->minToroidWidth, (v)->minToroidWidth = v, "Minimum Toroid Width");
        addInteger("min_lining_thickness", ()->minLiningThickness, (v)->minLiningThickness = v, "Minimum Lining Thickness");
        addInteger("max_inner_radius", ()->maxInnerRadius, (v)->maxInnerRadius = v, "Maximum Inner Radius");
        addInteger("max_core_size", ()->maxCoreSize, (v)->maxCoreSize = v, "Maximum Core Size");
        addInteger("max_toroid_width", ()->maxToroidWidth, (v)->maxToroidWidth = v, "Maximum Toroid Width");
        addInteger("max_lining_thickness", ()->maxLiningThickness, (v)->maxLiningThickness = v, "Maximum Lining Thickness");
        addFloat("sparsity_penalty_multiplier", ()->sparsityPenaltyMultiplier, (v)->sparsityPenaltyMultiplier = v, "Sparsity Penalty Multiplier");
        addFloat("sparsity_penalty_threshold", ()->sparsityPenaltyThreshold, (v)->sparsityPenaltyThreshold = v, "Sparsity Penalty Threshold");
        addInteger("cooling_efficiency_leniency", ()->coolingEfficiencyLeniency, (v)->coolingEfficiencyLeniency = v, "Cooling Efficiency Leniency", "The size of the \"safe zone\" around 0 H/t before you get overheating and overcooling penalties");
    }
}