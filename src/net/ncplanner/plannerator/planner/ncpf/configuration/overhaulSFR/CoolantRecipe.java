package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.MultiblockRecipeElement;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CoolantRecipeStatsModule;
public class CoolantRecipe extends NCPFElement implements MultiblockRecipeElement{
    public CoolantRecipeStatsModule stats = new CoolantRecipeStatsModule();
    public DisplayNamesModule names = new DisplayNamesModule();
    public TextureModule texture = new TextureModule();
    public CoolantRecipe(){}
    public CoolantRecipe(NCPFElementDefinition definition){
        super(definition);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        stats = getModule(CoolantRecipeStatsModule::new);
        names = getModule(DisplayNamesModule::new);
        texture = getModule(TextureModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(stats, names, texture);
        super.convertToObject(ncpf);
    }
    @Override
    public String getTitle(){
        return "Coolant Recipe";
    }
}