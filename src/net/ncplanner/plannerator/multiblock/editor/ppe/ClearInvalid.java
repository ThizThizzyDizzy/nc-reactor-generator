package net.ncplanner.plannerator.multiblock.editor.ppe;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
public class ClearInvalid extends PostProcessingEffect{
    public ClearInvalid(){
        super("Remove Invalid Blocks", true, true, true);
    }
    @Override
    public void apply(Multiblock multiblock, MultiblockGenerator generator){
        multiblock.forEachPosition((x, y, z) -> {
            AbstractBlock b = multiblock.getBlock(x, y, z);
            if(b==null)return;
            if(!b.isValid())multiblock.setBlock(x, y, z, null);
        });
    }
    @Override
    public boolean defaultEnabled(){
        return true;
    }
}