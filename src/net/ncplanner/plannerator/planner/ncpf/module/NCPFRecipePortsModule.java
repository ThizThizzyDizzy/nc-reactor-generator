package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockReference;
public abstract class NCPFRecipePortsModule<BlockElement extends NCPFElement> extends NCPFSettingsModule{
    public BlockReference<BlockElement> input;
    public BlockReference<BlockElement> output;
    public NCPFRecipePortsModule(String name){
        super(name);
        addReference("input", () -> input, (v) -> input = BlockReference.create((BlockElement)v), BlockReference<BlockElement>::new, (r) -> input = r, "Input");
        addReference("output", () -> output, (v) -> output = BlockReference.create((BlockElement)v), BlockReference<BlockElement>::new, (r) -> output = r, "Output");
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
