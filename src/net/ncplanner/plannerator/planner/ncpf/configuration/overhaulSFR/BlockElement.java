package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
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
public class BlockElement extends NCPFElement implements BlockRecipesElement{
    public DisplayNamesModule names = new DisplayNamesModule();
    public TextureModule texture = new TextureModule();
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
    public BlockElement(){}
    public BlockElement(NCPFElementDefinition definition){
        super(definition);
    }
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
        if(fuelCell!=null)fuels = getRecipes(Fuel::new);
        if(irradiator!=null)irradiatorRecipes = getRecipes(IrradiatorRecipe::new);
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        setModules(names, texture, conductor, casing, coolantVent, controller, fuelCell, irradiator, reflector, moderator, neutronShield, heatsink, neutronSource, port, recipePorts);
        super.setReferences(lst);
        if(parent!=null){
            fuels = parent.fuels;
            irradiatorRecipes = parent.irradiatorRecipes;
        }
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture, conductor, casing, coolantVent, controller, fuelCell, irradiator, reflector, moderator, neutronShield, heatsink, neutronSource, port, recipePorts);
        setRecipes(fuels, irradiatorRecipes);
        super.convertToObject(ncpf);
    }
    @Override
    public List<? extends NCPFElement> getBlockRecipes(){
        if(fuelCell!=null)return fuels;
        if(irradiator!=null)return irradiatorRecipes;
        if(parent!=null)return parent.getBlockRecipes();
        return null;
    }
    public boolean blocksLOS(){
        return fuelCell!=null||irradiator!=null||reflector!=null;
    }
    public boolean createsCluster(){
        return fuelCell!=null||irradiator!=null||neutronShield!=null;
    }
}