package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Recipe;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulFusionSettingsModule;
public class OverhaulFusionConfiguration extends NCPFConfiguration{
    public ConfigurationMetadataModule metadata = new ConfigurationMetadataModule();
    public OverhaulFusionSettingsModule settings = new OverhaulFusionSettingsModule();
    public List<Block> blocks = new ArrayList<>();
    public List<CoolantRecipe> coolantRecipes = new ArrayList<>();
    public List<Recipe> recipes = new ArrayList<>();
    public OverhaulFusionConfiguration(){
        super("plannerator:fusion_test");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        metadata = getModule(ConfigurationMetadataModule::new);
        settings = getModule(OverhaulFusionSettingsModule::new);
        blocks = ncpf.getDefinedNCPFList("blocks", blocks, Block::new);
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
    public List<NCPFElement>[] getMultiblockRecipes(){
        return new List[]{recipes, coolantRecipes};
    }
}