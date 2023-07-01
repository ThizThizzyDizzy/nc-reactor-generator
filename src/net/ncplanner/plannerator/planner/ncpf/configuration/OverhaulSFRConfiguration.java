package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulSFRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.OverhaulSFRSettingsModule;
public class OverhaulSFRConfiguration extends NCPFOverhaulSFRConfiguration{
    public OverhaulSFRSettingsModule settings;
    public List<Block> blocks;
    public List<CoolantRecipe> coolantRecipes;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        settings = getModule(OverhaulSFRSettingsModule::new);
        blocks = copyList(super.blocks, Block::new);
        coolantRecipes = copyList(super.coolantRecipes, CoolantRecipe::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModule(settings);
        super.blocks = copyList(blocks, NCPFElement::new);
        super.coolantRecipes = copyList(coolantRecipes, NCPFElement::new);
        super.convertToObject(ncpf);
    }
}