package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulTurbineConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulTurbineSettingsModule;
public class OverhaulTurbineConfiguration extends NCPFOverhaulTurbineConfiguration{
    public ConfigurationMetadataModule metadata = new ConfigurationMetadataModule();
    public OverhaulTurbineSettingsModule settings = new OverhaulTurbineSettingsModule();
    public List<Block> blocks = new ArrayList<>();
    public List<Recipe> recipes = new ArrayList<>();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        metadata = getModule(ConfigurationMetadataModule::new);
        settings = getModule(OverhaulTurbineSettingsModule::new);
        blocks = copyList(super.blocks, Block::new);
        recipes = copyList(super.recipes, Recipe::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(metadata, settings);
        super.blocks = copyList(blocks, NCPFElement::new);
        super.recipes = copyList(recipes, NCPFElement::new);
        super.convertToObject(ncpf);
    }
    @Override
    public List<NCPFElement>[] getMultiblockRecipes(){
        return new List[]{recipes};
    }
}