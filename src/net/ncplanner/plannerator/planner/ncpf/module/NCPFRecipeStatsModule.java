package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public abstract class NCPFRecipeStatsModule extends NCPFModule implements ElementStatsModule{
    public NCPFRecipeStatsModule(String name){
        super(name);
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new UnsupportedOperationException("Recipe stats may not be overwritten!");
    }
}