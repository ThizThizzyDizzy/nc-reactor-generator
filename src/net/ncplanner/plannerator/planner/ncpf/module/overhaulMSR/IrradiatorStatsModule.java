package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
public class IrradiatorStatsModule extends NCPFStatsModule{
    public float efficiency;
    public float heat;
    public NCPFElementReference output;
    public IrradiatorStatsModule(){
        super("nuclearcraft:overhaul_msr:irradiator_stats");
        addFloat("efficiency", ()->efficiency, (v)->efficiency = v, "Efficiency");
        addFloat("heat", ()->heat, (v)->heat = v, "Heat");
        addReference("output", () -> output, (elem) -> output = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output = r, "Output");
    }
    @Override
    public String getFriendlyName(){
        return "Irradiator Stats";
    }
}