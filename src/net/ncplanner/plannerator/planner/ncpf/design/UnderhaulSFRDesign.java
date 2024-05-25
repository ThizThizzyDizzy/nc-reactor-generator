package net.ncplanner.plannerator.planner.ncpf.design;
import java.util.Set;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFUnderhaulSFRDesign;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.ActiveCoolerRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ActiveCoolerModule;
public class UnderhaulSFRDesign extends MultiblockDesign<NCPFUnderhaulSFRDesign, UnderhaulSFR>{
    public Fuel fuel;
    public BlockElement[][][] design;
    public ActiveCoolerRecipe[][][] recipes;
    public UnderhaulSFRDesign(NCPFFile file){
        super(file);
        definition = new NCPFUnderhaulSFRDesign(file);
    }
    public UnderhaulSFRDesign(NCPFFile file, int x, int y, int z){
        this(file);
        design = new BlockElement[x+2][y+2][z+2];
        recipes = new ActiveCoolerRecipe[x+2][y+2][z+2];
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        fuel = copy(definition.fuel, Fuel::new);
        match3DArray(definition.design, design = new BlockElement[definition.design.length][definition.design[0].length][definition.design[0][0].length], file.getConfiguration(UnderhaulSFRConfiguration::new).blocks);
        match3DArrayConditional(definition.blockRecipes, recipes = new ActiveCoolerRecipe[definition.design.length][definition.design[0].length][definition.design[0][0].length], design, (BlockElement cooler)->matchElement(cooler).activeCoolerRecipes, (BlockElement cooler)->matchModule(cooler, ActiveCoolerModule::new));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.fuel = fuel;
        definition.design = design;
        definition.blockRecipes = combine3DArraysInto(definition.blockRecipes = new NCPFElement[design.length][design[0].length][design[0][0].length], recipes);
        super.convertToObject(ncpf);
    }
    public BlockElement matchElement(BlockElement block){
        UnderhaulSFRConfiguration config = file.getConfiguration(UnderhaulSFRConfiguration::new);
        if(config.settings==null)block = config.convertElement(block, Core.project.getConfiguration(UnderhaulSFRConfiguration::new));
        return block;
    }
    boolean matchModule(BlockElement block, Supplier<NCPFModule> module){
        block = matchElement(block);
        return block.hasModule(module);
    }
    @Override
    public UnderhaulSFR convertToMultiblock(){
        UnderhaulSFR sfr = new UnderhaulSFR(file.conglomeration, design.length-2, design[0].length-2, design[0][0].length-2, fuel);
        for(int x = 0; x<design.length; x++){
            for(int y = 0; y<design[x].length; y++){
                for(int z = 0; z<design[x][y].length; z++){
                    if(design[x][y][z]==null)continue;
                    Block block = new Block(file.conglomeration, x, y, z, design[x][y][z]);
                    block.recipe = recipes[x][y][z];
                    sfr.setBlock(x, y, z, block);
                }
            }
        }
        return sfr;
    }
    @Override
    public void convertElements(){
        UnderhaulSFRConfiguration config = file.getConfiguration(UnderhaulSFRConfiguration::new);
        convertElements(design, config);
        convertRecipes(design, recipes, (b)->b.activeCoolerRecipes, config);
        fuel = convertElement(fuel, config);
    }
    @Override
    public Set<NCPFElementDefinition> getElements(){
        Set<NCPFElementDefinition> elems = super.getElements();
        if(fuel!=null)elems.add(fuel.definition);
        getElements(design, elems);
        getElements(recipes, elems);
        return elems;
    }
}