package net.ncplanner.plannerator.planner.ncpf.design;
import java.util.Set;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.Block;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFOverhaulTurbineDesign;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe;
public class OverhaulTurbineDesign extends MultiblockDesign<NCPFOverhaulTurbineDesign, OverhaulTurbine>{
    public Recipe recipe;
    public BlockElement[][][] design;
    public OverhaulTurbineDesign(NCPFFile file){
        super(file);
        definition = new NCPFOverhaulTurbineDesign(file);
    }
    public OverhaulTurbineDesign(NCPFFile file, int x, int y, int z){
        this(file);
        design = new BlockElement[x+2][y+2][z+2];
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        recipe = definition.recipe.copyTo(Recipe::new);
        match3DArray(definition.design, design = new BlockElement[definition.design.length][definition.design[0].length][definition.design[0][0].length], file.getConfiguration(OverhaulTurbineConfiguration::new).blocks);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.recipe = recipe;
        definition.design = design;
        super.convertToObject(ncpf);
    }
    @Override
    public OverhaulTurbine toMultiblock(){
        OverhaulTurbine turbine = new OverhaulTurbine(file.conglomeration, design.length-2, design[0][0].length-2, recipe);
        for(int x = 0; x<design.length; x++){
            for(int y = 0; y<design[x].length; y++){
                for(int z = 0; z<design[x][y].length; z++){
                    if(design[x][y][z]==null)continue;
                    Block block = new Block(file.conglomeration, x, y, z, design[x][y][z]);
                    turbine.setBlock(x, y, z, block);
                }
            }
        }
        return turbine;
    }
    @Override
    public void convertElements(){
        OverhaulTurbineConfiguration config = file.getConfiguration(OverhaulTurbineConfiguration::new);
        convertElements(design, config);
        recipe = convertElement(recipe, config);
    }
    @Override
    public Set<NCPFElementDefinition> getElements(){
        Set<NCPFElementDefinition> elems = super.getElements();
        if(recipe!=null)elems.add(recipe.definition);
        getElements(design, elems);
        return elems;
    }
}