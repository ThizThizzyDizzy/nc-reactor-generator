package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
public class BlockReference extends NCPFElementReference{
    public Block block;
    @Override
    public void setReferences(List<NCPFElement> elements){
        super.setReferences(elements);
        block = target.copyTo(Block::new);
    }
}