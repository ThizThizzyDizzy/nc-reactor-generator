package net.ncplanner.plannerator.planner.ncpf.design;
import java.util.Set;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFOverhaulMSRDesign;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.HeaterRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.FuelVesselModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.HeaterModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.IrradiatorModule;
public class OverhaulMSRDesign extends MultiblockDesign<NCPFOverhaulMSRDesign, OverhaulMSR>{
    public BlockElement[][][] design;
    public Fuel[][][] fuels;
    public IrradiatorRecipe[][][] irradiatorRecipes;
    public HeaterRecipe[][][] heaterRecipes;
    public OverhaulMSRDesign(NCPFFile file){
        super(file);
        definition = new NCPFOverhaulMSRDesign(file);
    }
    public OverhaulMSRDesign(NCPFFile file, int x, int y, int z){
        this(file);
        design = new BlockElement[x+2][y+2][z+2];
        fuels = new Fuel[x+2][y+2][z+2];
        irradiatorRecipes = new IrradiatorRecipe[x+2][y+2][z+2];
        heaterRecipes = new HeaterRecipe[x+2][y+2][z+2];
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        match3DArray(definition.design, design = new BlockElement[definition.design.length][definition.design[0].length][definition.design[0][0].length], file.getConfiguration(OverhaulMSRConfiguration::new).blocks);
        match3DArrayConditional(definition.blockRecipes, fuels = new Fuel[definition.design.length][definition.design[0].length][definition.design[0][0].length], design, (BlockElement vessel)->matchElement(vessel).fuels, (BlockElement vessel)->matchModule(vessel, FuelVesselModule::new));
        match3DArrayConditional(definition.blockRecipes, irradiatorRecipes = new IrradiatorRecipe[definition.design.length][definition.design[0].length][definition.design[0][0].length], design, (BlockElement irradiator)->matchElement(irradiator).irradiatorRecipes, (BlockElement irradiator)->matchModule(irradiator, IrradiatorModule::new));
        match3DArrayConditional(definition.blockRecipes, heaterRecipes = new HeaterRecipe[definition.design.length][definition.design[0].length][definition.design[0][0].length], design, (BlockElement heater)->matchElement(heater).heaterRecipes, (BlockElement heater)->matchModule(heater, HeaterModule::new));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.design = design;
        definition.blockRecipes = combine3DArraysInto(definition.blockRecipes = new NCPFElement[design.length][design[0].length][design[0][0].length], fuels, irradiatorRecipes, heaterRecipes);
        super.convertToObject(ncpf);
    }
    public BlockElement matchElement(BlockElement block){
        OverhaulMSRConfiguration config = file.getConfiguration(OverhaulMSRConfiguration::new);
        if(config.settings==null)block = config.convertElement(block, Core.project.getConfiguration(OverhaulMSRConfiguration::new));
        return block;
    }
    public boolean matchModule(BlockElement block, Supplier<NCPFModule> module){
        block = matchElement(block);
        return block.hasModule(module)||block.parent!=null&&block.parent.hasModule(module);
    }
    @Override
    public OverhaulMSR convertToMultiblock(){
        OverhaulMSR msr = new OverhaulMSR(file.conglomeration, design.length-2, design[0].length-2, design[0][0].length-2);
        for(int x = 0; x<design.length; x++){
            for(int y = 0; y<design[x].length; y++){
                for(int z = 0; z<design[x][y].length; z++){
                    if(design[x][y][z]==null)continue;
                    Block block = new Block(file.conglomeration, x, y, z, design[x][y][z]);
                    block.fuel = fuels[x][y][z];
                    block.irradiatorRecipe = irradiatorRecipes[x][y][z];
                    block.heaterRecipe = heaterRecipes[x][y][z];
                    msr.setBlock(x, y, z, block);
                }
            }
        }
        return msr;
    }
    @Override
    public void convertElements(){
        OverhaulMSRConfiguration config = file.getConfiguration(OverhaulMSRConfiguration::new);
        convertElements(design, config);
        convertRecipes(design, fuels, (b)->b.fuels, config);
        convertRecipes(design, irradiatorRecipes, (b)->b.irradiatorRecipes, config);
        convertRecipes(design, heaterRecipes, (b)->b.heaterRecipes, config);
    }
    @Override
    public Set<NCPFElementDefinition> getElements(){
        Set<NCPFElementDefinition> elems = super.getElements();
        getElements(design, elems);
        getElements(fuels, elems);
        getElements(irradiatorRecipes, elems);
        getElements(heaterRecipes, elems);
        return elems;
    }
}