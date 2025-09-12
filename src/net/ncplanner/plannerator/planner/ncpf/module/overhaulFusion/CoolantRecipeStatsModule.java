package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.planner.module.FusionTestModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = FusionTestModule.class)
public class CoolantRecipeStatsModule extends NCPFStatsModule{
    public int heat;
    public float outputRatio;
    public NCPFElementReference output;
    public CoolantRecipeStatsModule(){
        super("plannerator:fusion_test:coolant_recipe_stats");
        addInteger("heat", () -> heat, (v) -> heat = v, "Heat");
        addFloat("output_ratio", () -> outputRatio, (v) -> outputRatio = v, "Output Ratio");
        addReference("output", () -> output, (elem) -> output = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output = r, "Output Fluid");
    }
    @Override
    public String getFriendlyName(){
        return "Coolant Recipe Stats";
    }
    public String getOutputName(){
        return output==null?null:output.definition.getName();
    }
    public String getOutputDisplayName(){
        return output==null?null:output.getDisplayName();
    }
}
