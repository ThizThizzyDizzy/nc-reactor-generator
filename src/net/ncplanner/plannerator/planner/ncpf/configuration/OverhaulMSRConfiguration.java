package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulMSRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulMSRSettingsModule;
public class OverhaulMSRConfiguration extends NCPFOverhaulMSRConfiguration{
    public ConfigurationMetadataModule metadata = new ConfigurationMetadataModule();
    public OverhaulMSRSettingsModule settings;
    public List<BlockElement> blocks = new ArrayList<>();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        metadata = getModule(ConfigurationMetadataModule::new);
        settings = getModule(OverhaulMSRSettingsModule::new);
        blocks = copyList(super.blocks, BlockElement::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(metadata, settings);
        super.blocks = copyList(blocks, NCPFElement::new);
        super.convertToObject(ncpf);
    }
    @Override
    public void conglomerate(DefinedNCPFModularObject obj){
        super.conglomerate(obj);
        OverhaulMSRConfiguration addon = (OverhaulMSRConfiguration) obj;
        conglomerateElementList(blocks, addon.blocks);
    }
    @Override
    public List<NCPFElement>[] getElements(){
        return new List[]{blocks};
    }
}