package multiblock.ppe;
import generator.Settings;
import multiblock.configuration.overhaul.fissionmsr.Block;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRSourceSaturate extends PostProcessingEffect<OverhaulMSR>{
    private final Block source;
    public MSRSourceSaturate(Block block){
        super("Saturate with "+block.getDisplayName(), true, false, true);
        this.source = block;
    }
    @Override
    public void apply(OverhaulMSR multiblock, Settings settings){
        multiblock.forEachPosition((x, y, z) -> {
            if(multiblock.getBlock(x, y, z)!=null&&multiblock.getBlock(x, y, z).template.fuelVessel)multiblock.getBlock(x, y, z).addNeutronSource(multiblock, source);
        });
    }
    @Override
    public boolean defaultEnabled(){
        return source.sourceEfficiency==1;
    }
}