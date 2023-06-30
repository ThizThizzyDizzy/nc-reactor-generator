package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulTurbineConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFOverhaulTurbineDesign extends NCPFCuboidalMultiblockDesign{
    public NCPFElement recipe;
    private NCPFElement[][][] recipes;
    public NCPFOverhaulTurbineDesign(){
        super("nuclearcraft:overhaul_turbine");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf, NCPFFile file){
        super.convertFromObject(ncpf, file);
        recipes = new NCPFElement[design.length][design[0].length][design[1].length];
        NCPFOverhaulTurbineConfiguration config = (NCPFOverhaulTurbineConfiguration) getConfiguration(file);
        NCPFList des = ncpf.getNCPFList("design");
        int i = -1;
        for(int x = 0; x<=design.length; x++){
            for(int y = 0; y<=design[x].length; y++){
                for(int z = 0; z<=design[z].length; z++){
                    design[x][y][z] = config.blocks.get(des.getInteger(++i));
                }
            }
        }
        recipe = config.recipes.get(ncpf.getInteger("recipe"));
    }
    @Override
    public void convertToObject(NCPFObject ncpf, NCPFFile file){
        super.convertToObject(ncpf, file);
        NCPFOverhaulTurbineConfiguration config = (NCPFOverhaulTurbineConfiguration) getConfiguration(file);
        NCPFList<Integer> des = new NCPFList<>();
        for(int x = 0; x<=design.length; x++){
            for(int y = 0; y<=design[x].length; y++){
                for(int z = 0; z<=design[z].length; z++){
                    des.add(config.blocks.indexOf(design[x][y][z]));
                }
            }
        }
        ncpf.setNCPFList("design", des);
        ncpf.setInteger("recipe", config.recipes.indexOf(recipe));
    }
}