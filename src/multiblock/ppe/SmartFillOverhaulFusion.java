package multiblock.ppe;
import generator.Settings;
import java.util.ArrayList;
import java.util.Random;
import multiblock.Range;
import multiblock.action.SetblockAction;
import multiblock.overhaul.fusion.Block;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
public class SmartFillOverhaulFusion extends PostProcessingEffect<OverhaulFusionReactor>{
    public SmartFillOverhaulFusion(){
        super("Smart Fill", false, true, false);
    }
    @Override
    public void apply(OverhaulFusionReactor multiblock, Settings settings){
        Random rand = new Random();
        final int coolingToAdd = multiblock.netHeat;
        int[] coolingAdded = new int[1];
        multiblock.forEachPosition((x, y, z) -> {
            if(coolingAdded[0]>=coolingToAdd)return;
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
                    coolingAdded[0]+=b.template.heatsinkCooling;
                    multiblock.queueAction(new SetblockAction(x, y, z, b.newInstance(x, y, z)));
                }
            }
        });
    }
}