package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
public class HeaterStatsModule extends NCPFStatsModule{
    public int cooling;
    public HeaterStatsModule(){
        super("nuclearcraft:overhaul_msr:heater_stats");
        addInteger("cooling", ()->cooling, (v)->cooling = v, "Cooling");
    }
}