package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
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
public class Block extends NCPFElement{
    public DisplayNamesModule names;
    public TextureModule texture;
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
    public List<Fuel> fuels;
    public List<HeaterRecipe> heaterRecipes;
    public List<IrradiatorRecipe> irradiatorRecipes;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        names = getModule(DisplayNamesModule::new);
        texture = getModule(TextureModule::new);
        conductor = getModule(ConductorModule::new);
        casing = getModule(CasingModule::new);
        controller = getModule(ControllerModule::new);
        fuelVessel = getModule(FuelVesselModule::new);
        irradiator = getModule(IrradiatorModule::new);
        reflector = getModule(ReflectorModule::new);
        moderator = getModule(ModeratorModule::new);
        neutronShield = getModule(NeutronShieldModule::new);
        heater = getModule(HeaterModule::new);
        neutronSource = getModule(NeutronSourceModule::new);
        port = getModule(PortModule::new);
        recipePorts = getModule(RecipePortsModule::new);
        withModule(NCPFBlockRecipesModule::new, (blockRecipes)->{
            if(fuelVessel!=null)fuels = copyList(blockRecipes, Fuel::new);
            if(heater!=null)heaterRecipes = copyList(blockRecipes, HeaterRecipe::new);
            if(irradiator!=null)irradiatorRecipes = copyList(blockRecipes, IrradiatorRecipe::new);
        });
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture, conductor, casing, controller, fuelVessel, irradiator, reflector, moderator, neutronShield, heater, neutronSource, port, recipePorts);
        if(fuelVessel!=null)withModuleOrCreate(NCPFBlockRecipesModule::new, (blockRecipes)->{
            copyList(fuels, blockRecipes);
        });
        if(heater!=null)withModuleOrCreate(NCPFBlockRecipesModule::new, (blockRecipes)->{
            copyList(heaterRecipes, blockRecipes);
        });
        if(irradiator!=null)withModuleOrCreate(NCPFBlockRecipesModule::new, (blockRecipes)->{
            copyList(irradiatorRecipes, blockRecipes);
        });
        super.convertToObject(ncpf);
    }
}