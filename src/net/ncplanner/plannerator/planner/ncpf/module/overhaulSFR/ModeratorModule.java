package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class ModeratorModule extends BlockFunctionModule implements ElementStatsModule{
    public int flux;
    public float efficiency;
    public ModeratorModule(){
        super("nuclearcraft:overhaul_sfr:moderator");
        addInteger("flux", ()->flux, (v)->flux = v, "Flux");
        addFloat("efficiency", ()->efficiency, (v)->efficiency = v, "Efficiency");
    }
    @Override
    public String getFunctionName(){
        return "Moderator";
    }
}