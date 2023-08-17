package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion;
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
    public DisplayNamesModule names = new DisplayNamesModule();
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
        names = getModule(DisplayNamesModule::new);
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
        return new Supplier[]{DisplayNamesModule::new, TextureModule::new, ConductorModule::new, ConnectorModule::new,
            CoreModule::new, PoloidalElectromagnetModule::new, ToroidalElectromagnetModule::new, HeatingBlanketModule::new,
            BreedingBlanketModule::new, ShieldingModule::new, ReflectorModule::new, HeatsinkModule::new};
    }
}