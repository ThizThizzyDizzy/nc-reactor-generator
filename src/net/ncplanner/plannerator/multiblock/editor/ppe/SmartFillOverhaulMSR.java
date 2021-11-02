package net.ncplanner.plannerator.multiblock.editor.ppe;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.Range;
import net.ncplanner.plannerator.multiblock.editor.action.SetblockAction;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
public class SmartFillOverhaulMSR extends PostProcessingEffect<OverhaulMSR>{
    public SmartFillOverhaulMSR(){
        super("Smart Fill", false, true, false);
    }
    @Override
    public void apply(OverhaulMSR multiblock, MultiblockGenerator generator){
        Random rand = new Random();
        final int coolingToAdd = multiblock.netHeat;
        int[] coolingAdded = new int[1];
        multiblock.forEachPosition((x, y, z) -> {
            if(coolingAdded[0]>=coolingToAdd)return;
            Block block = multiblock.getBlock(x, y, z);
            if(block==null||block.isConductor()||block.isInert()){
                ArrayList<Block> available = new ArrayList<>();
                for(Range<net.ncplanner.plannerator.multiblock.Block> range : generator.getAllowedBlocks()){
                    if(range.max!=Integer.MAX_VALUE&&multiblock.count(range.obj)>=range.max)continue;
                    Block blok = (Block)range.obj;
                    if(blok.isHeater()&&multiblock.isValid(blok, x, y, z))available.add(blok);
                }
                if(!available.isEmpty()){
                    Block b = available.get(rand.nextInt(available.size()));
                    coolingAdded[0]+=b.template.heaterCooling;
                    multiblock.queueAction(new SetblockAction(x, y, z, b));
                }
            }
        });
    }
}