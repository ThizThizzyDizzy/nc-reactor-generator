package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class BlockReference extends NCPFElementReference{
    public Block block;
    public BlockReference(){}
    public BlockReference(Block block){
        super(block.definition);
        this.block = block;
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        target = block;
        super.convertToObject(ncpf);
    }
    @Override
    public void setReferences(List<NCPFElement> elements){
        super.setReferences(elements);
        block = target.copyTo(Block::new);
    }
}