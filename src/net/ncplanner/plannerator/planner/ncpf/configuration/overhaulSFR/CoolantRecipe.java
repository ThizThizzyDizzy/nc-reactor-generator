package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CoolantRecipeStatsModule;
public class CoolantRecipe extends NCPFElement{
    public CoolantRecipeStatsModule stats;
    public DisplayNamesModule names;
    public TextureModule texture;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        stats = getModule(CoolantRecipeStatsModule::new);
        names = getModule(DisplayNamesModule::new);
        texture = getModule(TextureModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(stats, names, texture);
        super.convertToObject(ncpf);
    }
}