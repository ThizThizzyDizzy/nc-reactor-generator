package net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ActiveCoolerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.FuelCellModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ModeratorModule;
public class BlockElement extends BlockRecipesElement{
    public CoolerModule cooler;
    public ActiveCoolerModule activeCooler;
    public FuelCellModule fuelCell;
    public ModeratorModule moderator;
    public CasingModule casing;
    public ControllerModule controller;
    public List<ActiveCoolerRecipe> activeCoolerRecipes = new ArrayList<>();
    public BlockElement(){
        definePlanneratorModule(() -> cooler, (m) -> cooler = m, CoolerModule::new);
        definePlanneratorModule(() -> activeCooler, (m) -> activeCooler = m, ActiveCoolerModule::new);
        definePlanneratorModule(() -> fuelCell, (m) -> fuelCell = m, FuelCellModule::new);
        definePlanneratorModule(() -> moderator, (m) -> moderator = m, ModeratorModule::new);
        definePlanneratorModule(() -> casing, (m) -> casing = m, CasingModule::new);
        definePlanneratorModule(() -> controller, (m) -> controller = m, ControllerModule::new);
        definePlanneratorRecipes(getClass(), () -> activeCooler, (e) -> e.activeCoolerRecipes, () -> null, (e, lst) -> e.activeCoolerRecipes = lst, ActiveCoolerRecipe::new);
    }
    @Override
    public BlockRecipesElement getParent(){
        return null;
    }
}
