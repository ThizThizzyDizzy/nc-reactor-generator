package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Recipe;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulFusionSettingsModule;
public class OverhaulFusionConfiguration extends NCPFConfiguration{
    public ConfigurationMetadataModule metadata = new ConfigurationMetadataModule();
    public OverhaulFusionSettingsModule settings;
    public List<BlockElement> blocks = new ArrayList<>();
    public List<CoolantRecipe> coolantRecipes = new ArrayList<>();
    public List<Recipe> recipes = new ArrayList<>();
    public OverhaulFusionConfiguration(){
        super("plannerator:fusion_test");
    }
    @Override
    public void init(){
        setModule(metadata);
        settings = setModule(new OverhaulFusionSettingsModule());
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        metadata = getModule(ConfigurationMetadataModule::new);
        settings = getModule(OverhaulFusionSettingsModule::new);
        blocks = ncpf.getDefinedNCPFList("blocks", blocks, BlockElement::new);
        coolantRecipes = ncpf.getDefinedNCPFList("coolant_recipes", coolantRecipes, CoolantRecipe::new);
        recipes = ncpf.getDefinedNCPFList("recipes", recipes, Recipe::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(metadata, settings);
        ncpf.setDefinedNCPFList("blocks", blocks);
        ncpf.setDefinedNCPFList("coolant_recipes", coolantRecipes);
        ncpf.setDefinedNCPFList("recipes", recipes);
        super.convertToObject(ncpf);
    }
    @Override
    public void conglomerate(DefinedNCPFModularObject obj){
        super.conglomerate(obj);
        OverhaulFusionConfiguration addon = (OverhaulFusionConfiguration)obj;
        conglomerateElementList(blocks, addon.blocks);
        conglomerateElementList(coolantRecipes, addon.coolantRecipes);
        conglomerateElementList(recipes, addon.recipes);
    }
    @Override
    public List<NCPFElement>[] getElements(){
        return new List[]{blocks,coolantRecipes,recipes};
    }
    @Override
    public Supplier<NCPFElement>[] getElementSuppliers(){
        return new Supplier[]{BlockElement::new, CoolantRecipe::new, Recipe::new};
    }
    @Override
    public List<NCPFElement>[] getMultiblockRecipes(){
        return new List[]{recipes, coolantRecipes};
    }
    @Override
    public void makePartial(List<Design> designs){
        makePartial(blocks, designs);
        blocks.forEach((t) -> t.makePartial(designs));
        makePartial(coolantRecipes, designs);
        makePartial(recipes, designs);
    }
    @Override
    public String getName(){
        return "Fusion Test Configuration";
    }
}