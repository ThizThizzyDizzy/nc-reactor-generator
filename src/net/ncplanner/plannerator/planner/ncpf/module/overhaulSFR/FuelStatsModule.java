package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class FuelStatsModule extends NCPFStatsModule{
    public float efficiency;
    public int heat;
    public int time;
    public int criticality;
    public boolean selfPriming;
    public NCPFElementReference output;
    public FuelStatsModule(){
        super("nuclearcraft:overhaul_sfr:fuel_stats");
        addFloat("efficiency", () -> efficiency, (v) -> efficiency = v, "Efficiency");
        addInteger("heat", () -> heat, (v) -> heat = v, "Heat");
        addInteger("time", () -> time, (v) -> time = v, "Time");
        addInteger("criticality", () -> criticality, (v) -> criticality = v, "Criticality");
        addBoolean("self_priming", () -> selfPriming, (v) -> selfPriming = v, "Self-Priming");
        addReference("output", () -> output, (elem) -> output = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output = r, "Output");
    }
    @Override
    public String getFriendlyName(){
        return "Fuel Stats";
    }
}
