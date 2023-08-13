package net.ncplanner.plannerator.multiblock.editor.ppe;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
public class SFRFill extends PostProcessingEffect<OverhaulSFR>{
    private final BlockElement block;
    public SFRFill(BlockElement block){
        super("Fill with "+block.getDisplayName(), true, true, false);
        this.block = block;
    }
    @Override
    public void apply(OverhaulSFR multiblock, MultiblockGenerator generator){
        multiblock.forEachPosition((x, y, z) -> {
            if(multiblock.getBlock(x, y, z)==null||multiblock.getBlock(x, y, z).isConductor())multiblock.setBlock(x, y, z, new Block(multiblock.getConfiguration(), x, y, z, block));
        });
    }
}