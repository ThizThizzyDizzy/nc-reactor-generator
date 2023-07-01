package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class OverhaulSFRSettingsModule extends NCPFModule{
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public int coolingEfficiencyLeniency;
    public float sparsityPenaltyMultiplier;
    public float sparsityPenaltyThreshold;
    public OverhaulSFRSettingsModule(){
        super("nuclearcraft:overhaul_sfr_configuration_settings");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        minSize = ncpf.getInteger("min_size");
        maxSize = ncpf.getInteger("max_size");
        neutronReach = ncpf.getInteger("neutron_reach");
        coolingEfficiencyLeniency = ncpf.getInteger("cooling_efficiency_leniency");
        sparsityPenaltyMultiplier = ncpf.getFloat("sparsity_penalty_multiplier");
        sparsityPenaltyThreshold = ncpf.getFloat("sparsity_penalty_threshold");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("min_size", minSize);
        ncpf.setInteger("max_size", maxSize);
        ncpf.setInteger("neutron_reach", neutronReach);
        ncpf.setInteger("cooling_efficiency_leniency", coolingEfficiencyLeniency);
        ncpf.setFloat("sparsity_penalty_multiplier", sparsityPenaltyMultiplier);
        ncpf.setFloat("sparsity_penalty_threshold", sparsityPenaltyThreshold);
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new UnsupportedOperationException("Configuration settings may not be overwritten!");
    }
}