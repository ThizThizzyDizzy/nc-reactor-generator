package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.RecipeElement;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.IrradiatorStatsModule;
public class IrradiatorRecipe extends RecipeElement implements IBlockRecipe{
    public IrradiatorStatsModule stats = new IrradiatorStatsModule();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        stats = getModule(IrradiatorStatsModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(stats);
        super.convertToObject(ncpf);
    }
    @Override
    public String getTitle(){
        return "Irradiator Recipe";
    }
    @Override
    public Supplier<NCPFModule>[] getPreferredModules(){
        return new Supplier[]{IrradiatorStatsModule::new};
    }
}