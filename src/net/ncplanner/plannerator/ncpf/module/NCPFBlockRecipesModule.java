package net.ncplanner.plannerator.ncpf.module;
import java.util.ArrayList;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFBlockRecipesModule extends NCPFModule{
    public ArrayList<NCPFElement> recipes;
    public NCPFBlockRecipesModule(){
        super("ncpf:block_recipes");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        recipes = ncpf.getDefinedNCPFList("recipes", recipes, NCPFElement::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFList("recipes", recipes);
    }
    @Override
    public void conglomerate(NCPFModule addon){
        conglomerateElementList(recipes, ((NCPFBlockRecipesModule)addon).recipes);
    }
}