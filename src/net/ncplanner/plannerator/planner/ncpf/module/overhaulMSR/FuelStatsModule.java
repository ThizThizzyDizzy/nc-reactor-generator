package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
public class FuelStatsModule extends NCPFStatsModule{
    public float efficiency;
    public int heat;
    public int time;
    public int criticality;
    public boolean selfPriming;
    public NCPFElementReference output;
    public FuelStatsModule(){
        super("nuclearcraft:overhaul_msr:fuel_stats");
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
