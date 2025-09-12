package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.planner.module.FusionTestModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = FusionTestModule.class)
public class BreedingBlanketStatsModule extends NCPFStatsModule{
    public boolean augmented;
    public float efficiency;
    public float heat;
    public BreedingBlanketStatsModule(){
        super("plannerator:fusion_test:breeding_blanket_stats");
        addFloat("efficiency", () -> efficiency, (v) -> efficiency = v, "Efficiency");
        addFloat("heat", () -> heat, (v) -> heat = v, "Heat");
        addBoolean("augmented", () -> augmented, (v) -> augmented = v, "Augmented");
    }
    @Override
    public String getFriendlyName(){
        return "Breeding Blanket Stats";
    }
}
