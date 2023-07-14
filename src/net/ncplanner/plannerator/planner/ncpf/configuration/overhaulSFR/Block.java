package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
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
public class Block extends NCPFElement{
    public DisplayNamesModule names = new DisplayNamesModule();
    public TextureModule texture = new TextureModule();
    public ConductorModule conductor = new ConductorModule();
    public CasingModule casing = new CasingModule();
    public CoolantVentModule coolantVent = new CoolantVentModule();
    public ControllerModule controller = new ControllerModule();
    public FuelCellModule fuelCell = new FuelCellModule();
    public IrradiatorModule irradiator = new IrradiatorModule();
    public ReflectorModule reflector = new ReflectorModule();
    public ModeratorModule moderator = new ModeratorModule();
    public NeutronShieldModule neutronShield = new NeutronShieldModule();
    public HeatsinkModule heatsink = new HeatsinkModule();
    public NeutronSourceModule neutronSource = new NeutronSourceModule();
    public PortModule port = new PortModule();
    public RecipePortsModule recipePorts = new RecipePortsModule();
    public List<Fuel> fuels = new ArrayList<>();
    public List<IrradiatorRecipe> irradiatorRecipes = new ArrayList<>();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        names = getModule(DisplayNamesModule::new);
        texture = getModule(TextureModule::new);
        conductor = getModule(ConductorModule::new);
        casing = getModule(CasingModule::new);
        coolantVent = getModule(CoolantVentModule::new);
        controller = getModule(ControllerModule::new);
        fuelCell = getModule(FuelCellModule::new);
        irradiator = getModule(IrradiatorModule::new);
        reflector = getModule(ReflectorModule::new);
        moderator = getModule(ModeratorModule::new);
        neutronShield = getModule(NeutronShieldModule::new);
        heatsink = getModule(HeatsinkModule::new);
        neutronSource = getModule(NeutronSourceModule::new);
        port = getModule(PortModule::new);
        recipePorts = getModule(RecipePortsModule::new);
        withModule(NCPFBlockRecipesModule::new, (blockRecipes)->{
            if(fuelCell!=null)fuels = copyList(blockRecipes, Fuel::new);
            if(irradiator!=null)irradiatorRecipes = copyList(blockRecipes, IrradiatorRecipe::new);
        });
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture, conductor, casing, coolantVent, controller, fuelCell, irradiator, reflector, moderator, neutronShield, heatsink, neutronSource, port, recipePorts);
        if(fuelCell!=null)withModuleOrCreate(NCPFBlockRecipesModule::new, (blockRecipes)->{
            copyList(fuels, blockRecipes);
        });
        if(irradiator!=null)withModuleOrCreate(NCPFBlockRecipesModule::new, (blockRecipes)->{
            copyList(irradiatorRecipes, blockRecipes);
        });
        super.convertToObject(ncpf);
    }
}