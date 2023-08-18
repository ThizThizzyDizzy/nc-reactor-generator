package net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ActiveCoolerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.FuelCellModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ModeratorModule;
public class BlockElement extends NCPFElement implements BlockRecipesElement{
    public DisplayNameModule names = new DisplayNameModule();
    public TextureModule texture = new TextureModule();
    public CoolerModule cooler;
    public ActiveCoolerModule activeCooler;
    public FuelCellModule fuelCell;
    public ModeratorModule moderator;
    public CasingModule casing;
    public ControllerModule controller;
    public List<ActiveCoolerRecipe> activeCoolerRecipes = new ArrayList<>();
    public BlockElement(){}
    public BlockElement(NCPFElementDefinition definition){
        super(definition);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        names = getModule(DisplayNameModule::new);
        texture = getModule(TextureModule::new);
        cooler = getModule(CoolerModule::new);
        activeCooler = getModule(ActiveCoolerModule::new);
        fuelCell = getModule(FuelCellModule::new);
        moderator = getModule(ModeratorModule::new);
        casing = getModule(CasingModule::new);
        controller = getModule(ControllerModule::new);
        if(activeCooler!=null)activeCoolerRecipes = getRecipes(ActiveCoolerRecipe::new);
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        setModules(names, texture, cooler, activeCooler, fuelCell, moderator, casing, controller);
        super.setReferences(lst);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture, cooler, activeCooler, fuelCell, moderator, casing, controller);
        setRecipes(activeCoolerRecipes);
        super.convertToObject(ncpf);
    }
    @Override
    public List<? extends NCPFElement> getBlockRecipes(){
        return activeCoolerRecipes;
    }
    public void makePartial(List<Design> designs){
        makePartial(activeCoolerRecipes, designs);
    }
    @Override
    public String getTitle(){
        return "Block";
    }
    @Override
    public Supplier<NCPFModule>[] getPreferredModules(){
        return new Supplier[]{DisplayNameModule::new, TextureModule::new, CoolerModule::new, ActiveCoolerModule::new,
            FuelCellModule::new, ModeratorModule::new, CasingModule::new, ControllerModule::new};
    }
}