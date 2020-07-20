package multiblock;
import java.util.ArrayList;
public class ActionResult<T extends Multiblock>{
    private ArrayList<Block> blocks = new ArrayList<>();
    private T multiblock;
    public ActionResult(T multiblock, ArrayList<Block> blocks){
        this.multiblock = multiblock;
        this.blocks = blocks;
    }
    public ArrayList<Block> getAffectedGroups(){
        if(blocks==null){
            return multiblock.getBlocks(true);
        }
        return multiblock.getAffectedGroups(blocks);
    }
}