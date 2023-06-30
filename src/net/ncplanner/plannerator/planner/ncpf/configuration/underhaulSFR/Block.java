package net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.FuelCellModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ModeratorModule;
public class Block extends NCPFElement{
    public DisplayNamesModule names;
    public TextureModule texture;
    public CoolerModule cooler;
    public FuelCellModule fuelCell;
    public ModeratorModule moderator;
    public CasingModule casing;
    public ControllerModule controller;
    public List<ActiveCoolerRecipe> recipes;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        names = getModule(DisplayNamesModule::new);
        texture = getModule(TextureModule::new);
        cooler = getModule(CoolerModule::new);
        fuelCell = getModule(FuelCellModule::new);
        moderator = getModule(ModeratorModule::new);
        casing = getModule(CasingModule::new);
        controller = getModule(ControllerModule::new);
        withModule(NCPFBlockRecipesModule::new, (blockRecipes)->{
            this.recipes = copyList(blockRecipes.recipes, ActiveCoolerRecipe::new);
        });
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture, cooler, fuelCell, moderator, casing, controller);
        if(recipes!=null)withModuleOrCreate(NCPFBlockRecipesModule::new, (blockRecipes)->{
            copyList(recipes, blockRecipes.recipes, NCPFElement::new);
        });
        super.convertToObject(ncpf);
    }
}