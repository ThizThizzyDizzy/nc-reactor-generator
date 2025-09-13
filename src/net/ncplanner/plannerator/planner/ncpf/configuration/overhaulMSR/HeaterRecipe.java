package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.RecipeElement;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.HeaterStatsModule;
public class HeaterRecipe extends RecipeElement implements IBlockRecipe{
    public HeaterStatsModule stats = new HeaterStatsModule();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        stats = getModule(HeaterStatsModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(stats);
        super.convertToObject(ncpf);
    }
    @Override
    public String getTitle(){
        return "Heater Recipe";
    }
    @Override
    public Supplier<NCPFModule>[] getPreferredModules(){
        return new Supplier[]{HeaterStatsModule::new};
    }
}