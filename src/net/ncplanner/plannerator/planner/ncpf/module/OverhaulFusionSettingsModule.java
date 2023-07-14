package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class OverhaulFusionSettingsModule extends NCPFModule{
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
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        minInnerRadius = ncpf.getInteger("min_inner_radius");
        maxInnerRadius = ncpf.getInteger("max_inner_radius");
        minCoreSize = ncpf.getInteger("min_core_size");
        maxCoreSize = ncpf.getInteger("max_core_size");
        minToroidWidth = ncpf.getInteger("min_toroid_width");
        maxToroidWidth = ncpf.getInteger("max_toroid_width");
        minLiningThickness = ncpf.getInteger("min_lining_thickness");
        maxLiningThickness = ncpf.getInteger("max_lining_thickness");
        sparsityPenaltyMultiplier = ncpf.getFloat("sparsity_penalty_multiplier");
        sparsityPenaltyThreshold = ncpf.getFloat("sparsity_penalty_threshold");
        coolingEfficiencyLeniency = ncpf.getInteger("cooling_efficiency_leniency");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("min_inner_radius", minInnerRadius);
        ncpf.setInteger("max_inner_radius", maxInnerRadius);
        ncpf.setInteger("min_core_size", minCoreSize);
        ncpf.setInteger("max_core_size", maxCoreSize);
        ncpf.setInteger("min_toroid_width", minToroidWidth);
        ncpf.setInteger("max_toroid_width", maxToroidWidth);
        ncpf.setInteger("min_lining_thickness", minLiningThickness);
        ncpf.setInteger("max_lining_thickness", maxLiningThickness);
        ncpf.setFloat("sparsity_penalty_multiplier", sparsityPenaltyMultiplier);
        ncpf.setFloat("sparsity_penalty_threshold", sparsityPenaltyThreshold);
        ncpf.setInteger("cooling_efficiency_leniency", coolingEfficiencyLeniency);
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new UnsupportedOperationException("Configuration settings may not be overwritten!");
    }
}