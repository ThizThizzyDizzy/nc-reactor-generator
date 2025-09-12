package net.ncplanner.plannerator.ncpf.module;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class NCPFBlockRecipesModule extends NCPFModule{
    public ArrayList<NCPFElement> recipes = new ArrayList<>();
    public NCPFBlockRecipesModule(){
        super("ncpf:block_recipes");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        recipes = ncpf.getDefinedNCPFList("recipes", NCPFElement::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFList("recipes", recipes);
    }
    @Override
    public void conglomerate(NCPFModule addon){
        conglomerateElementList(recipes, ((NCPFBlockRecipesModule)addon).recipes);
    }
    @Override
    public void setReferences(List<NCPFElement> lst, boolean soft){
        for(NCPFElement recipe : recipes)recipe.setReferences(lst, soft);
    }
}
