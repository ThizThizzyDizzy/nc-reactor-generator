package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulSFRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulSFRSettingsModule;
public class OverhaulSFRConfiguration extends NCPFOverhaulSFRConfiguration{
    public ConfigurationMetadataModule metadata = new ConfigurationMetadataModule();
    public OverhaulSFRSettingsModule settings;
    public List<BlockElement> blocks = new ArrayList<>();
    public List<CoolantRecipe> coolantRecipes = new ArrayList<>();
    public OverhaulSFRConfiguration(){
        setModule(metadata);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        metadata = getModule(ConfigurationMetadataModule::new);
        settings = getModule(OverhaulSFRSettingsModule::new);
        blocks = copyList(super.blocks, BlockElement::new);
        coolantRecipes = copyList(super.coolantRecipes, CoolantRecipe::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(metadata, settings);
        super.blocks = copyList(blocks, NCPFElement::new);
        super.coolantRecipes = copyList(coolantRecipes, NCPFElement::new);
        super.convertToObject(ncpf);
    }
    @Override
    public void conglomerate(DefinedNCPFModularObject obj){
        super.conglomerate(obj);
        OverhaulSFRConfiguration addon = (OverhaulSFRConfiguration) obj;
        conglomerateElementList(blocks, addon.blocks);
        conglomerateElementList(coolantRecipes, addon.coolantRecipes);
    }
    @Override
    public List<NCPFElement>[] getMultiblockRecipes(){
        return new List[]{coolantRecipes};
    }
    @Override
    public List<NCPFElement>[] getElements(){
        return new List[]{blocks,coolantRecipes};
    }
    @Override
    public void makePartial(List<Design> designs){
        makePartial(blocks, designs);
        blocks.forEach((t) -> t.makePartial(designs));
        makePartial(coolantRecipes, designs);
    }
}