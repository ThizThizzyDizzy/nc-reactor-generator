package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
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
public class BlockElement extends NCPFElement implements BlockRecipesElement{
    public DisplayNamesModule names = new DisplayNamesModule();
    public TextureModule texture = new TextureModule();
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
        if(fuelVessel!=null)fuels = getRecipes(Fuel::new);
        if(heater!=null)heaterRecipes = getRecipes(HeaterRecipe::new);
        if(irradiator!=null)irradiatorRecipes = getRecipes(IrradiatorRecipe::new);
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        setModules(names, texture, conductor, casing, controller, fuelVessel, irradiator, reflector, moderator, neutronShield, heater, neutronSource, port, recipePorts);
        super.setReferences(lst);
        if(parent!=null){
            fuels = parent.fuels;
            irradiatorRecipes = parent.irradiatorRecipes;
            heaterRecipes = parent.heaterRecipes;
        }
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture, conductor, casing, controller, fuelVessel, irradiator, reflector, moderator, neutronShield, heater, neutronSource, port, recipePorts);
        setRecipes(fuels, heaterRecipes, irradiatorRecipes);
        super.convertToObject(ncpf);
    }
    @Override
    public List<? extends NCPFElement> getBlockRecipes(){
        if(fuelVessel!=null)return fuels;
        if(irradiator!=null)return irradiatorRecipes;
        if(heater!=null)return heaterRecipes;
        if(parent!=null)return parent.getBlockRecipes();
        return null;
    }
    public boolean blocksLOS(){
        return fuelVessel!=null||irradiator!=null||reflector!=null;
    }
    public boolean createsCluster(){
        return fuelVessel!=null||irradiator!=null||neutronShield!=null;
    }
    public void makePartial(List<Design> designs){
        makePartial(fuels, designs);
        makePartial(heaterRecipes, designs);
        makePartial(irradiatorRecipes, designs);
    }
    @Override
    public String getTitle(){
        return "Block";
    }
    @Override
    public Supplier<NCPFModule>[] getPreferredModules(){
        return new Supplier[]{DisplayNamesModule::new, TextureModule::new, ConductorModule::new, CasingModule::new,
            ControllerModule::new, FuelVesselModule::new, IrradiatorModule::new, ReflectorModule::new, ModeratorModule::new,
            NeutronShieldModule::new, HeaterModule::new, NeutronSourceModule::new, PortModule::new, RecipePortsModule::new};
    }
}