package net.ncplanner.plannerator.multiblock.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.Multiblock;
public class ActionResult<T extends Multiblock>{
    private ArrayList<AbstractBlock> blocks = new ArrayList<>();
    private T multiblock;
    public ActionResult(T multiblock, ArrayList<AbstractBlock> blocks){
        this.multiblock = multiblock;
        this.blocks = blocks;
    }
    /**
     * @return null if all blocks were affected
     */
    public ArrayList<AbstractBlock> getAffectedGroups(){
        if(blocks==null){
            return null;
        }
        return multiblock.getAffectedGroups(blocks);
    }
    /**
     * @return null if all blocks were affected
     */
    public ArrayList<AbstractBlock> getAffectedBlocks(){
        if(blocks==null){
            return null;
        }
        return blocks;
    }
}