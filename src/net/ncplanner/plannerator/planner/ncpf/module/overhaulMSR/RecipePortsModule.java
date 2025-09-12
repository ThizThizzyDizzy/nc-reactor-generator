package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFRecipePortsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class RecipePortsModule extends NCPFRecipePortsModule<BlockElement>{
    public RecipePortsModule(){
        super("nuclearcraft:overhaul_msr:recipe_ports");
    }
    @Override
    public void setLocalReferences(DefinedNCPFModularObject parentObject){
        if(input.block!=null&&output.block!=null){
            input.block.toggled = output.block;
            output.block.unToggled = input.block;
            input.block.parent = output.block.parent = (BlockElement)parentObject;
        }
    }
}
