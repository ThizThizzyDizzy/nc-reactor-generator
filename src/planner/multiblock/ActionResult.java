package planner.multiblock;
import java.util.ArrayList;
import java.util.List;
public class ActionResult<T extends Multiblock>{
    boolean fullReset = false;
    public ArrayList<Block> blocks = new ArrayList<>();
    private T multiblock;
    public ActionResult(T multiblock, List affectedBlocks){
        this.multiblock = multiblock;
        if(affectedBlocks.isEmpty()){
            fullReset = true;
            return;
        }
        for(Object o : affectedBlocks){
            if(o==multiblock){
                fullReset = true;
                return;
            }
            if(o instanceof Block){
                blocks.add((Block) o);
            }
            if(o instanceof int[]){
                int[] i = (int[]) o;
                blocks.add(multiblock.getBlock(i[0], i[1], i[2]));
            }
        }
        for(Block block : blocks){
            if(block==null){
                fullReset = true;
                return;
            }
            if(block instanceof planner.multiblock.underhaul.fissionsfr.Block){
                if(((planner.multiblock.underhaul.fissionsfr.Block)block).template.cooling==0){
                    fullReset = true;
                    return;
                }
            }
            if(block instanceof planner.multiblock.overhaul.fissionsfr.Block){
                if(((planner.multiblock.overhaul.fissionsfr.Block)block).template.cooling==0){
                    fullReset = true;
                    return;
                }
            }
            if(block instanceof planner.multiblock.overhaul.fissionmsr.Block){
                if(((planner.multiblock.overhaul.fissionmsr.Block)block).template.cooling==0){
                    fullReset = true;
                    return;
                }
            }
        }
    }
    public ArrayList<Block> getAffectedBlocks(){
        if(fullReset)return multiblock.getBlocks();
        return blocks;
    }
    public ArrayList<Block> getAffectedGroups(){
        if(fullReset)return multiblock.getBlocks();
        return multiblock.getAffectedGroups(blocks);
    }
}