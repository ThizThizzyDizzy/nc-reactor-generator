package net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule;
public class ActiveCoolerRecipe extends NCPFElement implements IBlockRecipe{
    public DisplayNameModule names = new DisplayNameModule();
    public TextureModule texture = new TextureModule();
    public CoolerModule stats = new CoolerModule();
    public ActiveCoolerRecipe(){}
    public ActiveCoolerRecipe(NCPFElementDefinition definition){
        super(definition);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        names = getModule(DisplayNameModule::new);
        texture = getModule(TextureModule::new);
        stats = getModule(CoolerModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture, stats);
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