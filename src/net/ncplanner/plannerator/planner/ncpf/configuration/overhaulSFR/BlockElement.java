package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.RecipePortsModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ConductorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CoolantVentModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.FuelCellModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.HeatsinkModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.IrradiatorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ModeratorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.NeutronShieldModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.NeutronSourceModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.PortModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ReflectorModule;
public class BlockElement extends BlockRecipesElement{
    public ConductorModule conductor;
    public CasingModule casing;
    public CoolantVentModule coolantVent;
    public ControllerModule controller;
    public FuelCellModule fuelCell;
    public IrradiatorModule irradiator;
    public ReflectorModule reflector;
    public ModeratorModule moderator;
    public NeutronShieldModule neutronShield;
    public HeatsinkModule heatsink;
    public NeutronSourceModule neutronSource;
    public PortModule port;
    public RecipePortsModule recipePorts;
    public List<Fuel> fuels = new ArrayList<>();
    public List<IrradiatorRecipe> irradiatorRecipes = new ArrayList<>();

    public BlockElement parent;//not saved, the parent block for this port
    public BlockElement unToggled;//not saved, the untoggled version of this block
    public BlockElement toggled;//not saved, the toggled version of this block
    public BlockElement(){
        definePlanneratorModule(() -> conductor, (m) -> conductor = m, ConductorModule::new);
        definePlanneratorModule(() -> casing, (m) -> casing = m, CasingModule::new);
        definePlanneratorModule(() -> coolantVent, (m) -> coolantVent = m, CoolantVentModule::new);
        definePlanneratorModule(() -> controller, (m) -> controller = m, ControllerModule::new);
        definePlanneratorModule(() -> fuelCell, (m) -> fuelCell = m, FuelCellModule::new);
        definePlanneratorModule(() -> irradiator, (m) -> irradiator = m, IrradiatorModule::new);
        definePlanneratorModule(() -> reflector, (m) -> reflector = m, ReflectorModule::new);
        definePlanneratorModule(() -> moderator, (m) -> moderator = m, ModeratorModule::new);
        definePlanneratorModule(() -> neutronShield, (m) -> neutronShield = m, NeutronShieldModule::new);
        definePlanneratorModule(() -> heatsink, (m) -> heatsink = m, HeatsinkModule::new);
        definePlanneratorModule(() -> neutronSource, (m) -> neutronSource = m, NeutronSourceModule::new);
        definePlanneratorModule(() -> port, (m) -> port = m, PortModule::new);
        definePlanneratorModule(() -> recipePorts, (m) -> recipePorts = m, RecipePortsModule::new);
        definePlanneratorRecipes(getClass(), () -> fuelCell, (e) -> e.fuels, () -> recipePorts, (e, lst) -> e.fuels = lst, Fuel::new);
        definePlanneratorRecipes(getClass(), () -> irradiator, (e) -> e.irradiatorRecipes, () -> recipePorts, (e, lst) -> e.irradiatorRecipes = lst, IrradiatorRecipe::new);
    }
    public boolean blocksLOS(){
        return fuelCell!=null||irradiator!=null||reflector!=null;
    }
    public boolean createsCluster(){
        return fuelCell!=null||irradiator!=null||neutronShield!=null;
    }
    @Override
    public BlockRecipesElement getParent(){
        return parent;
    }
}
