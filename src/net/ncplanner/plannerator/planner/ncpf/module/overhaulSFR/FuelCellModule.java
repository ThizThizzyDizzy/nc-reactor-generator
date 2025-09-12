package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.RecipesBlockModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class FuelCellModule extends BlockFunctionModule implements RecipesBlockModule{
    public FuelCellModule(){
        super("nuclearcraft:overhaul_sfr:fuel_cell");
    }
    @Override
    public String getFunctionName(){
        return "Fuel Cell";
    }
    @Override
    public Supplier<NCPFElement> getRecipeElement(){
        return Fuel::new;
    }
}
