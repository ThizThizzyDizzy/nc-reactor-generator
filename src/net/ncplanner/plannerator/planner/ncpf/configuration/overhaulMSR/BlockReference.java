package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class BlockReference extends NCPFElementReference{
    public static BlockReference create(BlockElement blockElement){
        BlockReference ref = new BlockReference(blockElement);
        ref.target = ref.block;
        return ref;
    }
    public BlockElement block;
    public BlockReference(){}
    public BlockReference(BlockElement block){
        super(block.definition);
        this.block = block;
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        if(block!=null)target = block;
        super.convertToObject(ncpf);
    }
    @Override
    public void setReferences(List<NCPFElement> elements){
        super.setReferences(elements);
        if(target instanceof BlockElement)block = (BlockElement)target;//it's not convertable when conglomerating
    }
}