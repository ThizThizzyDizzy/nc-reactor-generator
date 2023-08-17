package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
public class IrradiatorStatsModule extends NCPFStatsModule{
    public float efficiency;
    public float heat;
    public IrradiatorStatsModule(){
        super("nuclearcraft:overhaul_sfr:irradiator_stats");
        addFloat("efficiency", ()->efficiency, (v)->efficiency = v, "Efficiency");
        addFloat("heat", ()->heat, (v)->heat = v, "Heat");
    }
}