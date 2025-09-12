package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
@RegisterWith(module = OverhaulModule.class)
public class SieveTrayModule extends BlockFunctionModule{
    public SieveTrayModule(){
        super("nuclearcraft:overhaul_distiller:sieve_tray");
    }
    @Override
    public String getFunctionName(){
        return "Sieve Tray";
    }
}
