package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.RecipePortsModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ConductorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.FuelVesselModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.HeaterModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.IrradiatorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ModeratorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.NeutronShieldModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.NeutronSourceModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.PortModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ReflectorModule;
public class BlockElement extends BlockRecipesElement{
    public ConductorModule conductor;
    public CasingModule casing;
    public ControllerModule controller;
    public FuelVesselModule fuelVessel;
    public IrradiatorModule irradiator;
    public ReflectorModule reflector;
    public ModeratorModule moderator;
    public NeutronShieldModule neutronShield;
    public HeaterModule heater;
    public NeutronSourceModule neutronSource;
    public PortModule port;
    public RecipePortsModule recipePorts;
    public List<Fuel> fuels = new ArrayList<>();
    public List<HeaterRecipe> heaterRecipes = new ArrayList<>();
    public List<IrradiatorRecipe> irradiatorRecipes = new ArrayList<>();

    public BlockElement parent;//not saved, the parent block for this port
    public BlockElement unToggled;//not saved, the untoggled version of this block
    public BlockElement toggled;//not saved, the toggled version of this block
    public BlockElement(){
        definePlanneratorModule(() -> conductor, (m) -> conductor = m, ConductorModule::new);
        definePlanneratorModule(() -> casing, (m) -> casing = m, CasingModule::new);
        definePlanneratorModule(() -> controller, (m) -> controller = m, ControllerModule::new);
        definePlanneratorModule(() -> fuelVessel, (m) -> fuelVessel = m, FuelVesselModule::new);
        definePlanneratorModule(() -> irradiator, (m) -> irradiator = m, IrradiatorModule::new);
        definePlanneratorModule(() -> reflector, (m) -> reflector = m, ReflectorModule::new);
        definePlanneratorModule(() -> moderator, (m) -> moderator = m, ModeratorModule::new);
        definePlanneratorModule(() -> neutronShield, (m) -> neutronShield = m, NeutronShieldModule::new);
        definePlanneratorModule(() -> heater, (m) -> heater = m, HeaterModule::new);
        definePlanneratorModule(() -> neutronSource, (m) -> neutronSource = m, NeutronSourceModule::new);
        definePlanneratorModule(() -> port, (m) -> port = m, PortModule::new);
        definePlanneratorModule(() -> recipePorts, (m) -> recipePorts = m, RecipePortsModule::new);
        definePlanneratorRecipes(getClass(), () -> fuelVessel, (e) -> e.fuels, () -> recipePorts, (e, lst) -> e.fuels = lst, Fuel::new);
        definePlanneratorRecipes(getClass(), () -> heater, (e) -> e.heaterRecipes, () -> recipePorts, (e, lst) -> e.heaterRecipes = lst, HeaterRecipe::new);
        definePlanneratorRecipes(getClass(), () -> irradiator, (e) -> e.irradiatorRecipes, () -> recipePorts, (e, lst) -> e.irradiatorRecipes = lst, IrradiatorRecipe::new);
    }
    public boolean blocksLOS(){
        return fuelVessel!=null||irradiator!=null||reflector!=null;
    }
    public boolean createsCluster(){
        return fuelVessel!=null||irradiator!=null||neutronShield!=null;
    }
    @Override
    public BlockRecipesElement getParent(){
        return parent;
    }
}
