package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.LegacyRecipeElement;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.IrradiatorStatsModule;
public class IrradiatorRecipe extends LegacyRecipeElement implements IBlockRecipe{
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