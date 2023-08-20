package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFUnderhaulSFRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.UnderhaulSFRSettingsModule;
public class UnderhaulSFRConfiguration extends NCPFUnderhaulSFRConfiguration{
    public ConfigurationMetadataModule metadata = new ConfigurationMetadataModule();
    public UnderhaulSFRSettingsModule settings;
    public List<BlockElement> blocks = new ArrayList<>();
    public List<Fuel> fuels = new ArrayList<>();
    @Override
    public void init(boolean addon){
        setModule(metadata);
        if(!addon)settings = setModule(new UnderhaulSFRSettingsModule());
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        metadata = getModule(ConfigurationMetadataModule::new);
        settings = getModule(UnderhaulSFRSettingsModule::new);
        blocks = copyList(super.blocks, BlockElement::new);
        fuels = copyList(super.fuels, Fuel::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(metadata, settings);
        super.blocks = copyList(blocks, NCPFElement::new);
        super.fuels = copyList(fuels, NCPFElement::new);
        super.convertToObject(ncpf);
    }
    @Override
    public void conglomerate(DefinedNCPFModularObject obj){
        super.conglomerate(obj);
        UnderhaulSFRConfiguration addon = (UnderhaulSFRConfiguration) obj;
        conglomerateElementList(blocks, addon.blocks);
        conglomerateElementList(fuels, addon.fuels);
    }
    @Override
    public List<NCPFElement>[] getMultiblockRecipes(){
        return new List[]{fuels};
    }
    @Override
    public List<NCPFElement>[] getElements(){
        return new List[]{blocks, fuels};
    }
    @Override
    public Supplier<NCPFElement>[] getElementSuppliers(){
        return new Supplier[]{BlockElement::new, Fuel::new};
    }
    @Override
    public void makePartial(List<Design> designs){
        makePartial(blocks, designs);
        blocks.forEach((t) -> t.makePartial(designs));
        makePartial(fuels, designs);
    }
}