package net.ncplanner.plannerator.planner.ncpf.design;
import java.util.Set;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.overhaul.distiller.Block;
import net.ncplanner.plannerator.multiblock.overhaul.distiller.OverhaulDistiller;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFOverhaulDistillerDesign;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulDistillerConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.DistillerRecipe;
@RegisterWith(module = OverhaulModule.class)
public class OverhaulDistillerDesign extends MultiblockDesign<NCPFOverhaulDistillerDesign, OverhaulDistiller>{
    public DistillerRecipe recipe;
    public BlockElement[][][] design;
    public OverhaulDistillerDesign(NCPFFile file){
        super(file);
        definition = new NCPFOverhaulDistillerDesign(file);
    }
    public OverhaulDistillerDesign(NCPFFile file, int x, int y, int z){
        this(file);
        design = new BlockElement[x+2][y+2][z+2];
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        recipe = definition.recipe.copyTo(DistillerRecipe::new);
        match3DArray(definition.design, design = new BlockElement[definition.design.length][definition.design[0].length][definition.design[0][0].length], file.getConfiguration(OverhaulDistillerConfiguration::new).blocks);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.recipe = recipe;
        definition.design = design;
        super.convertToObject(ncpf);
    }
    public BlockElement matchElement(BlockElement block){
        OverhaulDistillerConfiguration config = file.getConfiguration(OverhaulDistillerConfiguration::new);
        if(config.settings==null)block = config.convertElement(block, Core.project.getConfiguration(OverhaulDistillerConfiguration::new));
        return block;
    }
    public boolean matchModule(BlockElement block, Supplier<NCPFModule> module){
        return matchElement(block).hasModule(module);
    }
    @Override
    public OverhaulDistiller convertToMultiblock(){
        OverhaulDistiller distiller = new OverhaulDistiller(file.conglomeration, design.length-2, design[0].length-2, design[0][0].length-2, recipe);
        for(int x = 0; x<design.length; x++){
            for(int y = 0; y<design[x].length; y++){
                for(int z = 0; z<design[x][y].length; z++){
                    if(design[x][y][z]==null)continue;
                    Block block = new Block(file.conglomeration, x, y, z, design[x][y][z]);
                    distiller.setBlock(x, y, z, block);
                }
            }
        }
        return distiller;
    }
    @Override
    public void convertElements(){
        OverhaulDistillerConfiguration config = file.getConfiguration(OverhaulDistillerConfiguration::new);
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