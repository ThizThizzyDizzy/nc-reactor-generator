package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class BlockReference<BlockElement extends NCPFElement> extends NCPFElementReference{
    public static <BlockElement extends NCPFElement> BlockReference<BlockElement> create(BlockElement blockElement){
        BlockReference<BlockElement> ref = new BlockReference<>(blockElement);
        ref.target = ref.block;
        return ref;
    }
    public BlockElement block;
    public BlockReference(){
    }
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
    public void setReferences(List<NCPFElement> elements, boolean soft){
        super.setReferences(elements, soft);
        // When conglomerating, it's only a `NCPFElement`. Otherwise, I can't instanceof BlockElement, but I can instanceof an intermediary
        if(target instanceof NamedTexturedNCPFElement)block = (BlockElement)target;
    }
}
