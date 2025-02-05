package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
public class HeaterStatsModule extends NCPFStatsModule{
    public int cooling;
    public NCPFElementReference output;
    public HeaterStatsModule(){
        super("nuclearcraft:overhaul_msr:heater_stats");
        addInteger("cooling", () -> cooling, (v) -> cooling = v, "Cooling");
        addReference("output", () -> output, (elem) -> output = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output = r, "Output Fluid");
    }
    @Override
    public String getFriendlyName(){
        return "Heater Stats";
    }
}
