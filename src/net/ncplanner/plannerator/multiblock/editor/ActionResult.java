package net.ncplanner.plannerator.multiblock.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
public class ActionResult<T extends Multiblock>{
    private ArrayList<Block> blocks = new ArrayList<>();
    private T multiblock;
    public ActionResult(T multiblock, ArrayList<Block> blocks){
        this.multiblock = multiblock;
        this.blocks = blocks;
    }
    /**
     * @return null if all blocks were affected
     */
    public ArrayList<Block> getAffectedGroups(){
        if(blocks==null){
            return null;
        }
        return multiblock.getAffectedGroups(blocks);
    }
    /**
     * @return null if all blocks were affected
     */
    public ArrayList<Block> getAffectedBlocks(){
        if(blocks==null){
            return null;
        }
        return blocks;
    }
}