package net.ncplanner.plannerator.multiblock.editor.ppe;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRSourceSaturate extends PostProcessingEffect<OverhaulMSR>{
    private final Block source;
    public MSRSourceSaturate(Block block){
        super("Saturate with "+block.getDisplayName(), true, false, true);
        this.source = block;
    }
    @Override
    public void apply(OverhaulMSR multiblock, MultiblockGenerator generator){
        multiblock.forEachPosition((x, y, z) -> {
            if(multiblock.getBlock(x, y, z)!=null&&multiblock.getBlock(x, y, z).template.fuelVessel)multiblock.getBlock(x, y, z).addNeutronSource(multiblock, source);
        });
    }
    @Override
    public boolean defaultEnabled(){
        return source.sourceEfficiency==1;
    }
}