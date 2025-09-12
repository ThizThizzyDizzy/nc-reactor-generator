package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class RecipeStatsModule extends NCPFStatsModule{
    public double timeMult = 1;
    public double powerMult = 1;
    public NCPFElementReference input1;
    public NCPFElementReference input2;
    public NCPFElementReference output1;
    public NCPFElementReference output2;
    public NCPFElementReference output3;
    public NCPFElementReference output4;
    public NCPFElementReference output5;
    public NCPFElementReference output6;
    public NCPFElementReference output7;
    public NCPFElementReference output8;
    public RecipeStatsModule(){
        super("nuclearcraft:overhaul_distiller:recipe_stats");
        addDouble("time", () -> timeMult, (v) -> timeMult = v, "Time Multiplier");
        addDouble("power", () -> powerMult, (v) -> powerMult = v, "Power Multiplier");
        addReference("input1", () -> input1, (elem) -> input1 = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> input1 = r, "Input Fluid 1");
        addReference("input2", () -> input2, (elem) -> input2 = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> input2 = r, "Input Fluid 2");
        addReference("output1", () -> output1, (elem) -> output1 = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output1 = r, "Output Fluid 1");
        addReference("output2", () -> output2, (elem) -> output2 = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output2 = r, "Output Fluid 2");
        addReference("output3", () -> output3, (elem) -> output3 = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output3 = r, "Output Fluid 3");
        addReference("output4", () -> output4, (elem) -> output4 = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output4 = r, "Output Fluid 4");
        addReference("output5", () -> output5, (elem) -> output5 = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output5 = r, "Output Fluid 5");
        addReference("output6", () -> output6, (elem) -> output6 = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output6 = r, "Output Fluid 6");
        addReference("output7", () -> output7, (elem) -> output7 = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output7 = r, "Output Fluid 7");
        addReference("output8", () -> output8, (elem) -> output8 = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output8 = r, "Output Fluid 8");
    }
    @Override
    public String getFriendlyName(){
        return "Recipe Stats";
    }
}
