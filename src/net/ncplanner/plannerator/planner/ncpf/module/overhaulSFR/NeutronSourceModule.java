package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class NeutronSourceModule extends BlockFunctionModule implements ElementStatsModule{
    public float efficiency;
    public NeutronSourceModule(){
        super("nuclearcraft:overhaul_sfr:neutron_source");
        addFloat("efficiency", () -> efficiency, (v) -> efficiency = v, "Efficiency");
    }
    @Override
    public String getFunctionName(){
        return "Neutron Source";
    }
}
