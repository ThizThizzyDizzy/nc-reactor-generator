package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class UnderhaulSFRSettingsModule extends NCPFModule{
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public float moderatorExtraPower;
    public float moderatorExtraHeat;
    public int activeCoolerRate;
    public UnderhaulSFRSettingsModule(){
        super("nuclearcraft:underhaul_sfr_configuration_settings");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        minSize = ncpf.getInteger("min_size");
        maxSize = ncpf.getInteger("max_size");
        neutronReach = ncpf.getInteger("neutron_reach");
        moderatorExtraPower = ncpf.getFloat("moderator_extra_power");
        moderatorExtraHeat = ncpf.getFloat("moderator_extra_heat");
        activeCoolerRate = ncpf.getInteger("active_cooler_rate");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("min_size", minSize);
        ncpf.setInteger("max_size", maxSize);
        ncpf.setInteger("neutron_reach", neutronReach);
        ncpf.setFloat("moderator_extra_power", moderatorExtraPower);
        ncpf.setFloat("moderator_extra_heat", moderatorExtraHeat);
        ncpf.setInteger("active_cooler_rate", activeCoolerRate);
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new UnsupportedOperationException("Configuration settings may not be overwritten!");
    }
}