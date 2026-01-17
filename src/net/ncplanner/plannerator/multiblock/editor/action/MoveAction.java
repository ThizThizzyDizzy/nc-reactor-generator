package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.planner.editor.Editor;
public class MoveAction extends Action<Multiblock>{
    private final Editor editor;
    private final int id;
    private final ArrayList<BlockPos> blocksToMove = new ArrayList<>();
    private final ArrayList<BlockPos> selection = new ArrayList<>();
    private final HashMap<BlockPos, AbstractBlock> was = new HashMap<>();
    private final int dx;
    private final int dy;
    private final int dz;
    public MoveAction(Editor editor, int id, Collection<BlockPos> blocksToMove, Collection<BlockPos> selection, int dy, int dx, int dz){
        synchronized(blocksToMove){
            this.blocksToMove.addAll(blocksToMove);
        }
        synchronized(selection){
            this.selection.addAll(selection);
        }
        this.dx = dy;
        this.dy = dx;
        this.dz = dz;
        this.editor = editor;
        this.id = id;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        ArrayList<BlockPos> movedSelection = new ArrayList<>();
        for(BlockPos loc : blocksToMove){
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
        for(BlockPos loc : selection){
            was.put(loc, multiblock.getBlock(loc));
            multiblock.setBlock(loc, null);
        }
        for(BlockPos loc : blocksToMove){
            AbstractBlock bl = null;
            for(BlockPos i : was.keySet()){
                if(i.equals(loc))bl = was.get(i);
            }
            if(multiblock.contains(loc.offset(dx,dy,dz))){
                multiblock.setBlock(loc.offset(dx,dy,dz), bl);
            }
        }
        synchronized(editor.getSelection(id)){
            editor.getSelection(id).clear();
            editor.getSelection(id).addAll(movedSelection);
        }
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
        for(BlockPos loc : blocksToMove){
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