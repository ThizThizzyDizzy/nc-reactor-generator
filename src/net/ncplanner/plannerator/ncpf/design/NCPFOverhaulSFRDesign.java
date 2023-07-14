package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulSFRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFOverhaulSFRDesign extends NCPFCuboidalMultiblockDesign{
    public NCPFElement coolantRecipe;
    public NCPFElement[][][] blockRecipes;
    public NCPFOverhaulSFRDesign(){
        super("nuclearcraft:overhaul_sfr");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        blockRecipes = new NCPFElement[design.length][design[0].length][design[1].length];
        NCPFOverhaulSFRConfiguration config = getConfiguration();
        ncpf.getDefined3DArray("design", design, config.blocks);
        ncpf.getRecipe3DArray("block_recipes", blockRecipes, design);
        coolantRecipe = config.coolantRecipes.get(ncpf.getInteger("coolant_recipe"));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        super.convertToObject(ncpf);
        NCPFOverhaulSFRConfiguration config = getConfiguration();
        ncpf.setDefined3DArray("design", design, config.blocks);
        ncpf.setRecipe3DArray("block_recipes", blockRecipes, design);
        ncpf.setInteger("coolant_recipe", config.coolantRecipes.indexOf(coolantRecipe));
    }
}