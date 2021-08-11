package multiblock.ppe;
import generator.MultiblockGenerator;
import java.util.ArrayList;
import java.util.Random;
import multiblock.Range;
import multiblock.action.SetblockAction;
import multiblock.underhaul.fissionsfr.Block;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
public class SmartFillUnderhaulSFR extends PostProcessingEffect<UnderhaulSFR>{
    public SmartFillUnderhaulSFR(){
        super("Smart Fill", false, true, false);
    }
    @Override
    public void apply(UnderhaulSFR multiblock, MultiblockGenerator generator){
        Random rand = new Random();
        final int coolingToAdd = multiblock.netHeat;
        int[] coolingAdded = new int[1];
        multiblock.forEachPosition((x, y, z) -> {
//            if(coolingAdded[0]>=coolingToAdd)return;//TODO an option for not-so-negative underhaul SFRs?
            Block block = multiblock.getBlock(x, y, z);
            if(block==null){
                ArrayList<Block> available = new ArrayList<>();
                for(Range<multiblock.Block> range : generator.getAllowedBlocks()){
                    if(range.max!=Integer.MAX_VALUE&&multiblock.count(range.obj)>=range.max)continue;
                    Block blok = (Block)range.obj;
                    if(blok.isCooler()&&multiblock.isValid(blok, x, y, z))available.add(blok);
                }
                if(!available.isEmpty()){
                    Block b = available.get(rand.nextInt(available.size()));
                    coolingAdded[0]+=b.template.cooling;
                    multiblock.queueAction(new SetblockAction(x, y, z, b));
                }
            }
        });
    }
}