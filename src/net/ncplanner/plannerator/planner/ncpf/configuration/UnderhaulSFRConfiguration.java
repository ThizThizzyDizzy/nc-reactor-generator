package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFUnderhaulSFRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.UnderhaulSFRSettingsModule;
public class UnderhaulSFRConfiguration extends NCPFUnderhaulSFRConfiguration{
    public ConfigurationMetadataModule metadata;
    public UnderhaulSFRSettingsModule settings;
    public List<Block> blocks;
    public List<Fuel> fuels;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        metadata = getModule(ConfigurationMetadataModule::new);
        settings = getModule(UnderhaulSFRSettingsModule::new);
        blocks = copyList(super.blocks, Block::new);
        fuels = copyList(super.fuels, Fuel::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(metadata, settings);
        super.blocks = copyList(blocks, NCPFElement::new);
        super.fuels = copyList(fuels, NCPFElement::new);
        super.convertToObject(ncpf);
    }
}