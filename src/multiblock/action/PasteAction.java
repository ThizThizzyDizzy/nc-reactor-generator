package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import planner.editor.ClipboardEntry;
public class PasteAction extends Action<Multiblock>{
    private final ArrayList<Block> was = new ArrayList<>();
    private final ArrayList<int[]> wasAir = new ArrayList<>();
    private final int x;
    private final int y;
    private final int z;
    private final ArrayList<ClipboardEntry> blocks;
    public PasteAction(ArrayList<ClipboardEntry> blocks, int x, int y, int z){
        this.blocks = blocks;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        was.clear();
        for(ClipboardEntry entry : blocks){
            int bx = entry.x+x;
            int by = entry.y+y;
            int bz = entry.z+z;
            if(!multiblock.contains(bx, by, bz))continue;
            if(allowUndo){
                Block bl = multiblock.getBlock(bx, by, bz);
                if(bl!=null)was.add(bl);
                else wasAir.add(new int[]{bx,by,bz});
            }
            multiblock.setBlock(bx, by, bz, entry.block);
        }
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(Block b : was){
            multiblock.setBlockExact(b.x, b.y, b.z, b);
        }
        for(int[] loc : wasAir){
            multiblock.setBlockExact(loc[0], loc[1], loc[2], null);
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){
        for(ClipboardEntry entry : this.blocks){
            if(multiblock.contains(entry.x+x, entry.y+y, entry.z+z)){
                Block block = multiblock.getBlock(entry.x+x, entry.y+y, entry.z+z);
                if(block==null)continue;
                if(!blocks.contains(block)){
                    blocks.add(block);
                }
            }
        }
    }
}