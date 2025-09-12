package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import net.ncplanner.plannerator.planner.module.UnderhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = UnderhaulModule.class)
public class FuelStatsModule extends NCPFStatsModule{
    public float power;
    public float heat;
    public int time;
    public FuelStatsModule(){
        super("nuclearcraft:underhaul_sfr:fuel_stats");
        addFloat("power", () -> power, (v) -> power = v, "Base Power");
        addFloat("heat", () -> heat, (v) -> heat = v, "Base Heat");
        addInteger("time", () -> time, (v) -> time = v, "Base Time");
    }
    @Override
    public String getFriendlyName(){
        return "Fuel Stats";
    }
}
