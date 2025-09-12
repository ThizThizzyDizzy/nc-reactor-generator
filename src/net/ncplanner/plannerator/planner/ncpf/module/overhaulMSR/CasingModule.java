package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class CasingModule extends BlockFunctionModule{
    public boolean edge;
    public CasingModule(){
        super("nuclearcraft:overhaul_msr:casing");
        addBoolean("edge", () -> edge, (v) -> edge = v, "Edge");
    }
    public CasingModule(boolean edge){
        this();
        this.edge = edge;
    }
    @Override
    public String getFunctionName(){
        return "Casing";
    }
}
