package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.RecipesBlockModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class FuelVesselModule extends BlockFunctionModule implements RecipesBlockModule{
    public FuelVesselModule(){
        super("nuclearcraft:overhaul_msr:fuel_vessel");
    }
    @Override
    public String getFunctionName(){
        return "Fuel Vessel";
    }
    @Override
    public Supplier<NCPFElement> getRecipeElement(){
        return Fuel::new;
    }
}
