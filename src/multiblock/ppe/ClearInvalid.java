package multiblock.ppe;
import generator.Settings;
import multiblock.Block;
import multiblock.Multiblock;
public class ClearInvalid extends PostProcessingEffect{
    public ClearInvalid(){
        super("Remove Invalid Blocks", true, true, true);
    }
    @Override
    public void apply(Multiblock multiblock, Settings settings){
        multiblock.forEachPosition((x, y, z) -> {
            Block b = multiblock.getBlock(x, y, z);
            if(b==null)return;
            if(!b.isValid())multiblock.setBlock(x, y, z, null);
        });
    }
    @Override
    public boolean defaultEnabled(){
        return true;
    }
}