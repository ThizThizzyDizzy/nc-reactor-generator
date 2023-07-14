package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulMSRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFOverhaulMSRDesign extends NCPFCuboidalMultiblockDesign{
    public NCPFElement coolantRecipe;
    public NCPFElement[][][] blockRecipes;
    public NCPFOverhaulMSRDesign(){
        super("nuclearcraft:overhaul_msr");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        blockRecipes = new NCPFElement[design.length][design[0].length][design[1].length];
        NCPFOverhaulMSRConfiguration config = getConfiguration();
        ncpf.getDefined3DArray("design", design, config.blocks);
        ncpf.getRecipe3DArray("block_recipes", blockRecipes, design);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        super.convertToObject(ncpf);
        NCPFOverhaulMSRConfiguration config = getConfiguration();
        ncpf.setDefined3DArray("design", design, config.blocks);
        ncpf.setRecipe3DArray("block_recipes", blockRecipes, design);
    }
}