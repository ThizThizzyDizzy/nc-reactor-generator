package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFSettingsModule;
public class RecipePortsModule extends NCPFSettingsModule{
    public BlockReference input;
    public BlockReference output;
    public RecipePortsModule(){
        super("nuclearcraft:overhaul_sfr:recipe_ports");
        addReference("input", ()->input, (v)->input = BlockReference.create((BlockElement)v), "Input");
        addReference("output", ()->output, (v)->output = BlockReference.create((BlockElement)v), "Output");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        input = ncpf.getDefinedNCPFObject("input", BlockReference::new);
        output = ncpf.getDefinedNCPFObject("output", BlockReference::new);
    }
    @Override
    public void setLocalReferences(DefinedNCPFModularObject parentObject){
        if(input.block!=null&&output.block!=null){
            input.block.toggled = output.block;
            output.block.unToggled = input.block;
            input.block.parent = output.block.parent = (BlockElement)parentObject;
        }
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new UnsupportedOperationException("Block ports may not be overwritten!");
    }
    @Override
    public String getFriendlyName(){
        return "Recipe Ports";
    }
}