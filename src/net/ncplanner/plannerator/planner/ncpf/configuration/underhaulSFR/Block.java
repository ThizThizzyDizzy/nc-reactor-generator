package net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ActiveCoolerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.FuelCellModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ModeratorModule;
public class Block extends NCPFElement implements BlockRecipesElement{
    public DisplayNamesModule names = new DisplayNamesModule();
    public TextureModule texture = new TextureModule();
    public CoolerModule cooler;
    public ActiveCoolerModule activeCooler;
    public FuelCellModule fuelCell;
    public ModeratorModule moderator;
    public CasingModule casing;
    public ControllerModule controller;
    public List<ActiveCoolerRecipe> activeCoolerRecipes = new ArrayList<>();
    public Block(){}
    public Block(NCPFElementDefinition definition){
        super(definition);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        names = getModule(DisplayNamesModule::new);
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
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture, cooler, activeCooler, fuelCell, moderator, casing, controller);
        setRecipes(activeCoolerRecipes);
        super.convertToObject(ncpf);
    }
    @Override
    public List<? extends NCPFElement> getBlockRecipes(){
        return activeCoolerRecipes;
    }
}