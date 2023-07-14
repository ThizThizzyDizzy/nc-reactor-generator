package net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ActiveCoolerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.FuelCellModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ModeratorModule;
public class Block extends NCPFElement{
    public DisplayNamesModule names = new DisplayNamesModule();
    public TextureModule texture = new TextureModule();
    public CoolerModule cooler = new CoolerModule();
    public ActiveCoolerModule activeCooler = new ActiveCoolerModule();
    public FuelCellModule fuelCell = new FuelCellModule();
    public ModeratorModule moderator = new ModeratorModule();
    public CasingModule casing = new CasingModule();
    public ControllerModule controller = new ControllerModule();
    public List<ActiveCoolerRecipe> activeCoolerRecipes = new ArrayList<>();
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
        withModule(NCPFBlockRecipesModule::new, (blockRecipes)->{
            activeCoolerRecipes = copyList(blockRecipes, ActiveCoolerRecipe::new);
        });
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture, cooler, activeCooler, fuelCell, moderator, casing, controller);
        if(activeCooler!=null)withModuleOrCreate(NCPFBlockRecipesModule::new, (blockRecipes)->{
            copyList(activeCoolerRecipes, blockRecipes);
        });
        super.convertToObject(ncpf);
    }
}