package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulSFRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFOverhaulSFRDesign extends NCPFCuboidalMultiblockDesign{
    public NCPFElement coolantRecipe;
    public NCPFElement[][][] blockRecipes;
    public NCPFOverhaulSFRDesign(){
        super("nuclearcraft:overhaul_sfr");
    }
    public NCPFOverhaulSFRDesign(NCPFFile file){
        this();
        this.file = file;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        blockRecipes = new NCPFElement[design.length][design[0].length][design[0][0].length];
        NCPFOverhaulSFRConfiguration config = getConfiguration();
        ncpf.getDefined3DArray("design", design, config.blocks);
        ncpf.getRecipe3DArray("block_recipes", blockRecipes, design);
        coolantRecipe = ncpf.getIndex("coolant_recipe", config.coolantRecipes);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        super.convertToObject(ncpf);
        NCPFOverhaulSFRConfiguration config = getConfiguration();
        ncpf.setDefined3DArray("design", design, config.blocks);
        ncpf.setRecipe3DArray("block_recipes", blockRecipes, design);
        ncpf.setIndex("coolant_recipe", coolantRecipe, config.coolantRecipes);
    }
}