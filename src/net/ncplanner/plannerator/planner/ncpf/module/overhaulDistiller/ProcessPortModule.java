package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class ProcessPortModule extends BlockFunctionModule{
    public boolean output;
    public ProcessPortModule(){
        super("nuclearcraft:overhaul_distiller:process_port");
        addBoolean("output", () -> output, (v) -> output = v, "Output");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        // Legacy distiller ports used two input tank indices followed by output indices.
        Boolean direction = ncpf.getBoolean("output");
        Integer index = ncpf.getInteger("index");
        output = direction!=null?direction:index!=null&&index>=2;
    }
    @Override
    public String getFunctionName(){
        return "Process Port";
    }
}
