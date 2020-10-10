package multiblock.ppe;
import generator.Settings;
import java.util.ArrayList;
import java.util.Random;
import multiblock.Range;
import multiblock.action.SetblockAction;
import multiblock.overhaul.fissionsfr.Block;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
public class SmartFillOverhaulSFR extends PostProcessingEffect<OverhaulSFR>{
    public SmartFillOverhaulSFR(){
        super("Smart Fill", false, true, false);
    }
    @Override
    public void apply(OverhaulSFR multiblock, Settings settings){
        Random rand = new Random();
        final int coolingToAdd = multiblock.netHeat;
        int coolingAdded = 0;
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    if(coolingAdded>=coolingToAdd)return;
                    Block block = multiblock.getBlock(x, y, z);
                    if(block==null||block.isConductor()||block.isInert()){
                        ArrayList<Block> available = new ArrayList<>();
                        for(Range<multiblock.Block> range : settings.getAllowedBlocks()){
                            if(range.max!=Integer.MAX_VALUE&&multiblock.count(range.obj)>=range.max)continue;
                            Block blok = (Block)range.obj;
                            if(blok.isHeatsink()&&multiblock.isValid(blok, x, y, z))available.add(blok);
                        }
                        if(!available.isEmpty()){
                            Block b = available.get(rand.nextInt(available.size()));
                            coolingAdded+=b.template.cooling;
                            multiblock.queueAction(new SetblockAction(x, y, z, b.newInstance(x, y, z)));
                        }
                    }
                }
            }
        }
    }
}