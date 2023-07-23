package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockReference;
public class RecipePortsModule extends NCPFModule{
    public BlockReference input;
    public BlockReference output;
    public RecipePortsModule(){
        super("nuclearcraft:overhaul_sfr:recipe_ports");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        input = ncpf.getDefinedNCPFObject("input", BlockReference::new);
        output = ncpf.getDefinedNCPFObject("output", BlockReference::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFObject("input", input);
        ncpf.setDefinedNCPFObject("output", output);
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        input.setReferences(lst);
        output.setReferences(lst);
    }
    @Override
    public void setLocalReferences(DefinedNCPFModularObject parentObject){
        if(input.block!=null&&output.block!=null){
            input.block.toggled = output.block;
            output.block.unToggled = input.block;
            input.block.parent = output.block.parent = (Block)parentObject;
        }
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new UnsupportedOperationException("Block ports may not be overwritten!");
    }
}