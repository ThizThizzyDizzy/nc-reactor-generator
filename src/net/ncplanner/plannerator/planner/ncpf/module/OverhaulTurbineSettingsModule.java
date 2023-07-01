package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class OverhaulTurbineSettingsModule extends NCPFModule{
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
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        minWidth = ncpf.getInteger("min_width");
        minLength = ncpf.getInteger("min_length");
        maxSize = ncpf.getInteger("max_size");
        fluidPerBlade = ncpf.getInteger("fluid_per_blade");
        throughputFactor = ncpf.getFloat("throughput_factor");
        powerBonus = ncpf.getFloat("power_bonus");
        throughputEfficiencyLeniencyMultiplier = ncpf.getFloat("throughput_efficiency_leniency_multiplier");
        throughputEfficiencyLeniencyThreshold = ncpf.getFloat("throughput_efficiency_leniency_threshold");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("min_width", minWidth);
        ncpf.setInteger("min_length", minLength);
        ncpf.setInteger("max_size", maxSize);
        ncpf.setInteger("fluid_per_blade", fluidPerBlade);
        ncpf.setFloat("throughput_factor", throughputFactor);
        ncpf.setFloat("power_bonus", powerBonus);
        ncpf.setFloat("throughput_efficiency_leniency_multiplier", throughputEfficiencyLeniencyMultiplier);
        ncpf.setFloat("throughput_efficiency_leniency_threshold", throughputEfficiencyLeniencyThreshold);
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new UnsupportedOperationException("Configuration settings may not be overwritten!");
    }
}