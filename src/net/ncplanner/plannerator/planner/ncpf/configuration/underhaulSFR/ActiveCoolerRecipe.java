package net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule;
public class ActiveCoolerRecipe extends NCPFElement{
    public DisplayNamesModule names = new DisplayNamesModule();
    public TextureModule texture = new TextureModule();
    public CoolerModule stats = new CoolerModule();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        names = getModule(DisplayNamesModule::new);
        texture = getModule(TextureModule::new);
        stats = getModule(CoolerModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture, stats);
        super.convertToObject(ncpf);
    }
}