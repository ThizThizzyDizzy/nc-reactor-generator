package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.NamedTexturedNCPFElement;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.FuelStatsModule;
public class Fuel extends NamedTexturedNCPFElement implements IBlockRecipe{
    public FuelStatsModule stats = new FuelStatsModule();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        stats = getModule(FuelStatsModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(stats);
        super.convertToObject(ncpf);
    }
    @Override
    public String getTitle(){
        return "Fuel";
    }
    @Override
    public Supplier<NCPFModule>[] getPreferredModules(){
        return new Supplier[]{FuelStatsModule::new};
    }
}