package net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.RecipeElement;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule;
public class ActiveCoolerRecipe extends RecipeElement implements IBlockRecipe{
    public CoolerModule stats = new CoolerModule();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        stats = getModule(CoolerModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(stats);
        super.convertToObject(ncpf);
    }
    @Override
    public String getTitle(){
        return "Active Cooler Recipe";
    }
    @Override
    public Supplier<NCPFModule>[] getPreferredModules(){
        return new Supplier[]{CoolerModule::new};
    }
}