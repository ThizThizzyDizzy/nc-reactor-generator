package net.ncplanner.plannerator.multiblock.editor.ppe;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
public class MSRFill extends PostProcessingEffect<OverhaulMSR>{
    private final BlockElement block;
    public MSRFill(BlockElement block){
        super("Fill with "+block.getDisplayName(), true, true, false);
        this.block = block;
    }
    @Override
    public void apply(OverhaulMSR multiblock, MultiblockGenerator generator){
        multiblock.forEachPosition((x, y, z) -> {
            if(multiblock.getBlock(x, y, z)==null||multiblock.getBlock(x,y,z).isConductor())multiblock.setBlock(x, y, z, new Block(multiblock.getConfiguration(), x, y, z, block));
        });
    }
}