package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import net.ncplanner.plannerator.planner.module.UnderhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = UnderhaulModule.class)
public class CoolerModule extends BlockRulesModule{
    public int cooling;
    public CoolerModule(){
        super("nuclearcraft:underhaul_sfr:cooler");
        addInteger("cooling", () -> cooling, (v) -> cooling = v, "Cooling");
    }
    @Override
    public String getFunctionName(){
        return "Cooler";
    }
    @Override
    public String getStatsTooltip(){
        return "Cooling: "+cooling+"H/t";
    }
}
