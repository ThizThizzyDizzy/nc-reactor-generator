package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.BreedingBlanketStatsModule;
public class BreedingBlanketRecipe extends NCPFElement implements IBlockRecipe{
    public BreedingBlanketStatsModule stats = new BreedingBlanketStatsModule();
    public DisplayNamesModule names = new DisplayNamesModule();
    public TextureModule texture = new TextureModule();
    public BreedingBlanketRecipe(){}
    public BreedingBlanketRecipe(NCPFElementDefinition definition){
        super(definition);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        stats = getModule(BreedingBlanketStatsModule::new);
        names = getModule(DisplayNamesModule::new);
        texture = getModule(TextureModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(stats, names, texture);
        super.convertToObject(ncpf);
    }
}