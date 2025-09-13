package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.MultiblockRecipeElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.RecipeElement;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.CoolantRecipeStatsModule;
public class CoolantRecipe extends RecipeElement implements MultiblockRecipeElement{
    public CoolantRecipeStatsModule stats = new CoolantRecipeStatsModule();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        stats = getModule(CoolantRecipeStatsModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(stats);
        super.convertToObject(ncpf);
    }
    @Override
    public String getTitle(){
        return "Coolant Recipe";
    }
    @Override
    public Supplier<NCPFModule>[] getPreferredModules(){
        return new Supplier[]{CoolantRecipeStatsModule::new};
    }
}