package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class ReflectorModule extends BlockFunctionModule implements ElementStatsModule{
    public float efficiency;
    public float reflectivity;
    public ReflectorModule(){
        super("nuclearcraft:overhaul_sfr:reflector");
        addFloat("efficiency", () -> efficiency, (v) -> efficiency = v, "Efficiency");
        addFloat("reflectivity", () -> reflectivity, (v) -> reflectivity = v, "Reflectivity");
    }
    @Override
    public String getFunctionName(){
        return "Reflector";
    }
}
