package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
public class BreedingBlanketStatsModule extends NCPFStatsModule{
    public boolean augmented;
    public float efficiency;
    public float heat;
    public BreedingBlanketStatsModule(){
        super("plannerator:fusion_test:breeding_blanket_stats");
        addFloat("efficiency", ()->efficiency, (v)->efficiency = v, "Efficiency");
        addFloat("heat", ()->heat, (v)->heat = v, "Heat");
        addBoolean("augmented", ()->augmented, (v)->augmented = v, "Augmented");
    }
}