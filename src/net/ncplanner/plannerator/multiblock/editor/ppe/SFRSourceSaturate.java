package net.ncplanner.plannerator.multiblock.editor.ppe;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
public class SFRSourceSaturate extends PostProcessingEffect<OverhaulSFR>{
    private final BlockElement source;
    public SFRSourceSaturate(BlockElement block){
        super("Saturate with "+block.getDisplayName(), true, false, true);
        this.source = block;
    }
    @Override
    public void apply(OverhaulSFR multiblock, MultiblockGenerator generator){
        multiblock.forEachPosition((x, y, z) -> {
            if(multiblock.getBlock(x, y, z)!=null&&multiblock.getBlock(x, y, z).template.fuelCell!=null)multiblock.getBlock(x, y, z).addNeutronSource(multiblock, source);
        });
    }
    @Override
    public boolean defaultEnabled(){
        return source.neutronSource.efficiency==1;
    }
}