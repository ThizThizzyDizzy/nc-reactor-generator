package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.MultiblockRecipeElement;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.RecipeStatsModule;
public class Recipe extends NCPFElement implements MultiblockRecipeElement{
    public DisplayNameModule names = new DisplayNameModule();
    public TextureModule texture = new TextureModule();
    public RecipeStatsModule stats = new RecipeStatsModule();
    public Recipe(){}
    public Recipe(NCPFElementDefinition definition){
        super(definition);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        stats = getModule(RecipeStatsModule::new);
        names = getModule(DisplayNameModule::new);
        texture = getModule(TextureModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(stats, names, texture);
        super.convertToObject(ncpf);
    }
    @Override
    public String getTitle(){
        return "Recipe";
    }
    @Override
    public Supplier<NCPFModule>[] getPreferredModules(){
        return new Supplier[]{DisplayNameModule::new, TextureModule::new, RecipeStatsModule::new};
    }
}