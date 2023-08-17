package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class ShieldingModule extends BlockFunctionModule implements ElementStatsModule{
    public float shieldiness;
    public ShieldingModule(){
        super("plannerator:fusion_test:shielding");
        addFloat("shieldiness", ()->shieldiness, (v)->shieldiness = v, "Shieldiness");
    }
    @Override
    public String getFunctionName(){
        return "Shielding";
    }
}