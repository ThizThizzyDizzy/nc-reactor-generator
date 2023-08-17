package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFSettingsModule;
public class FuelStatsModule extends NCPFSettingsModule implements ElementStatsModule{
    public float power;
    public float heat;
    public int time;
    public FuelStatsModule(){
        super("nuclearcraft:underhaul_sfr:fuel_stats");
        addFloat("power", ()->power, (v)->power = v, "Base Power");
        addFloat("heat", ()->heat, (v)->heat = v, "Base Heat");
        addInteger("time", ()->time, (v)->time = v, "Base Time");
    }
}