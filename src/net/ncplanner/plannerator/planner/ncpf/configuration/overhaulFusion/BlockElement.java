package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.BreedingBlanketModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ConductorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ConnectorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.HeatingBlanketModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.HeatsinkModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.PoloidalElectromagnetModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ReflectorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ShieldingModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ToroidalElectromagnetModule;
public class BlockElement extends NCPFElement implements BlockRecipesElement{
    public DisplayNameModule names = new DisplayNameModule();
    public TextureModule texture = new TextureModule();
    public ConductorModule conductor;
    public ConnectorModule connector;
    public CoreModule core;
    public PoloidalElectromagnetModule poloid;
    public ToroidalElectromagnetModule toroid;
    public HeatingBlanketModule heatingBlanket;
    public BreedingBlanketModule breedingBlanket;
    public ShieldingModule shielding;
    public ReflectorModule reflector;
    public HeatsinkModule heatsink;
    public List<BreedingBlanketRecipe> breedingBlanketRecipes = new ArrayList<>();
    public BlockElement(){}
    public BlockElement(NCPFElementDefinition definition){
        super(definition);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        names = getModule(DisplayNameModule::new);
        texture = getModule(TextureModule::new);
        conductor = getModule(ConductorModule::new);
        connector = getModule(ConnectorModule::new);
        core = getModule(CoreModule::new);
        poloid = getModule(PoloidalElectromagnetModule::new);
        toroid = getModule(ToroidalElectromagnetModule::new);
        heatingBlanket = getModule(HeatingBlanketModule::new);
        breedingBlanket = getModule(BreedingBlanketModule::new);
        shielding = getModule(ShieldingModule::new);
        reflector = getModule(ReflectorModule::new);
        heatsink = getModule(HeatsinkModule::new);
        if(breedingBlanket!=null)breedingBlanketRecipes = getRecipes(BreedingBlanketRecipe::new);
    }
    @Override
    public void conglomerate(DefinedNCPFModularObject addon){
        super.conglomerate(addon);
        if(breedingBlanket!=null)breedingBlanketRecipes = getRecipes(BreedingBlanketRecipe::new);
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        setModules(names, texture, conductor, connector, core, poloid, toroid, heatingBlanket, breedingBlanket, shielding, reflector, heatsink);
        super.setReferences(lst);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture, conductor, connector, core, poloid, toroid, heatingBlanket, breedingBlanket, shielding, reflector, heatsink);
        setRecipes(breedingBlanketRecipes);
        super.convertToObject(ncpf);
    }
    @Override
    public List<? extends NCPFElement> getBlockRecipes(){
        return breedingBlanketRecipes;
    }
    @Override
    public void clearBlockRecipes(){
        breedingBlanketRecipes.clear();
    }
    public void makePartial(List<Design> designs){
        makePartial(breedingBlanketRecipes, designs);
    }
    public boolean createsCluster(){
        return heatingBlanket!=null||breedingBlanket!=null;//I dunno, this sounds about right
    }
    @Override
    public String getTitle(){
        return "Block";
    }
    @Override
    public Supplier<NCPFModule>[] getPreferredModules(){
        return new Supplier[]{ConductorModule::new, ConnectorModule::new,
            CoreModule::new, PoloidalElectromagnetModule::new, ToroidalElectromagnetModule::new, HeatingBlanketModule::new,
            BreedingBlanketModule::new, ShieldingModule::new, ReflectorModule::new, HeatsinkModule::new};
    }
    @Override
    public void removeModule(NCPFModule module){
        if(module==conductor)conductor = null;
        if(module==connector)connector = null;
        if(module==core)core = null;
        if(module==poloid)poloid = null;
        if(module==toroid)toroid = null;
        if(module==heatingBlanket)heatingBlanket = null;
        if(module==breedingBlanket)breedingBlanket = null;
        if(module==shielding)shielding = null;
        if(module==reflector)reflector = null;
        if(module==heatsink)heatsink = null;
        super.removeModule(module);
    }
}