package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.FuelStatsModule;
public class Fuel extends NCPFElement implements IBlockRecipe{
    public DisplayNamesModule names = new DisplayNamesModule();
    public TextureModule texture = new TextureModule();
    public FuelStatsModule stats = new FuelStatsModule();
    public Fuel(){}
    public Fuel(NCPFElementDefinition definition){
        super(definition);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        stats = getModule(FuelStatsModule::new);
        names = getModule(DisplayNamesModule::new);
        texture = getModule(TextureModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(stats, names, texture);
        super.convertToObject(ncpf);
    }
    @Override
    public String getTitle(){
        return "Fuel";
    }
    @Override
    public Supplier<NCPFModule>[] getPreferredModules(){
        return new Supplier[]{DisplayNamesModule::new, TextureModule::new, FuelStatsModule::new};
    }
}