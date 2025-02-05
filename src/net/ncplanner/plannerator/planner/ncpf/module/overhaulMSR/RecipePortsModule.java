package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFSettingsModule;
public class RecipePortsModule extends NCPFSettingsModule{
    public BlockReference input;
    public BlockReference output;
    public RecipePortsModule(){
        super("nuclearcraft:overhaul_msr:recipe_ports");
        addReference("input", () -> input, (v) -> input = BlockReference.create((BlockElement)v), BlockReference::new, (r) -> input = r, "Input");
        addReference("output", () -> output, (v) -> output = BlockReference.create((BlockElement)v), BlockReference::new, (r) -> output = r, "Output");
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
