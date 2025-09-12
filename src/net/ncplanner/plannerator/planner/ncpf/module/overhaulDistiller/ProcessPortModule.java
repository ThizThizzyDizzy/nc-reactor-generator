package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class ProcessPortModule extends BlockFunctionModule{
    public int index;
    public ProcessPortModule(){
        super("nuclearcraft:overhaul_distiller:process_port");
        addInteger("index", () -> index, (v) -> index = v, "Index");
    }
    @Override
    public String getFunctionName(){
        return "Process Port";
    }
}
