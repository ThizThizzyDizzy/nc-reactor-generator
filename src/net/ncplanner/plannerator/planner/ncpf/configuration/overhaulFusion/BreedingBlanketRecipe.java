package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.NamedTexturedNCPFElement;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.BreedingBlanketStatsModule;
public class BreedingBlanketRecipe extends NamedTexturedNCPFElement implements IBlockRecipe{
    public BreedingBlanketStatsModule stats = new BreedingBlanketStatsModule();
    public BreedingBlanketRecipe(){}
    public BreedingBlanketRecipe(NCPFElementDefinition definition){
        super(definition);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        stats = getModule(BreedingBlanketStatsModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(stats);
        super.convertToObject(ncpf);
    }
    @Override
    public String getTitle(){
        return "Breeding Blanket Recipe";
    }
    @Override
    public Supplier<NCPFModule>[] getPreferredModules(){
        return new Supplier[]{BreedingBlanketStatsModule::new};
    }
}