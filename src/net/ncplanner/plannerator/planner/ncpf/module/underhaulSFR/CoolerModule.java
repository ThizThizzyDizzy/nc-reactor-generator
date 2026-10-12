package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.ActiveCoolerRecipe;
import net.ncplanner.plannerator.planner.module.UnderhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = UnderhaulModule.class)
public class CoolerModule extends BlockRulesModule{
    public int cooling;
    public CoolerModule(){
        super("nuclearcraft:underhaul_sfr:cooler");
        addInteger("cooling", () -> cooling, (v) -> cooling = v, "Cooling", "Heat removed per tick for passive coolers, or per mB of coolant for active cooler recipes");
    }
    @Override
    public String getFunctionName(){
        return "Cooler";
    }
    @Override
    public String getStatsTooltip(){
        return getStatsTooltip(null);
    }
    @Override
    public String getStatsTooltip(NCPFElement element){
        return "Cooling: "+cooling+(element instanceof ActiveCoolerRecipe?"H/mB":"H/t");
    }
}
