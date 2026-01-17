package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.planner.editor.Editor;
public class CopyAction extends Action<Multiblock>{
    private final Editor editor;
    private final int id;
    private final ArrayList<BlockPos> blocksToCopy = new ArrayList<>();
    private final ArrayList<BlockPos> selection = new ArrayList<>();
    private final HashMap<BlockPos, AbstractBlock> was = new HashMap<>();
    private final int dx;
    private final int dy;
    private final int dz;
    public CopyAction(Editor editor, int id, Collection<BlockPos> blocksToMove, Collection<BlockPos> selection, int dx, int dy, int dz){
        this.blocksToCopy.addAll(blocksToMove);
        this.selection.addAll(selection);
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.editor = editor;
        this.id = id;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        ArrayList<BlockPos> movedSelection = new ArrayList<>();
        for(BlockPos loc : blocksToCopy){
            BlockPos movedLoc = loc.offset(dx,dy,dz);
            if(multiblock.contains(movedLoc)){
                AbstractBlock to = multiblock.getBlock(movedLoc);
                was.put(movedLoc, to);
            }
        }
        for(BlockPos loc : selection){
            BlockPos movedLoc = loc.offset(dx,dy,dz);
            if(multiblock.contains(movedLoc)){
                movedSelection.add(movedLoc);
            }
        }
        for(BlockPos loc : blocksToCopy){
            AbstractBlock bl = multiblock.getBlock(loc);
            for(BlockPos i : was.keySet()){
                if(i.equals(loc))bl = was.get(i);
            }
            if(multiblock.contains(loc.offset(dx,dy,dz))){
                multiblock.setBlock(loc.offset(dx,dy,dz), bl);
            }
        }
        synchronized(editor.getSelection(id)){
            if(!editor.isShiftPressed(id))editor.getSelection(id).clear();
        }
        editor.addSelection(id, movedSelection);
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(BlockPos loc : was.keySet()){
            multiblock.setBlockExact(loc, was.get(loc));
        }
        synchronized(editor.getSelection(id)){
            editor.getSelection(id).clear();
            editor.getSelection(id).addAll(selection);
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<AbstractBlock> blocks){
        for(BlockPos loc : blocksToCopy){
            AbstractBlock from = multiblock.getBlock(loc);
            if(from!=null)blocks.add(from);
            if(multiblock.contains(loc.offset(dx,dy,dz))){
                AbstractBlock to = multiblock.getBlock(loc.offset(dx,dy,dz));
                if(to==null)continue;
                if(!blocks.contains(to)){
                    blocks.add(to);
                }
            }
        }
    }
}