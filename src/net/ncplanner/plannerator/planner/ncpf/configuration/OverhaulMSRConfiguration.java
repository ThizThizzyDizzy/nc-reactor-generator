package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulMSRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulMSRSettingsModule;
public class OverhaulMSRConfiguration extends NCPFOverhaulMSRConfiguration{
    public OverhaulMSRSettingsModule settings;
    public List<Block> blocks;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        settings = getModule(OverhaulMSRSettingsModule::new);
        blocks = copyList(super.blocks, Block::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModule(settings);
        super.blocks = copyList(blocks, NCPFElement::new);
        super.convertToObject(ncpf);
    }
}