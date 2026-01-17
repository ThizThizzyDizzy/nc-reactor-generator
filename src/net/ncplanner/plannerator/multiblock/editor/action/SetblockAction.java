package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
public class SetblockAction extends Action{
    public final BlockPos pos;
    public final AbstractBlock block;
    private AbstractBlock was = null;
    public SetblockAction(BlockPos pos, AbstractBlock block){
        this.pos = pos;
        this.block = block;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.getBlock(pos);
        multiblock.setBlock(pos, block);
    }
    @Override
    public void doUndo(Multiblock multiblock){
        multiblock.setBlockExact(pos, was);
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList blocks){
        AbstractBlock b = multiblock.getBlock(pos);
        if(b!=null)blocks.add(b);
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof SetblockAction){
            SetblockAction other = (SetblockAction)obj;
            if(block==null&&other.block!=null)return false;
            return pos.equals(other.pos)&&(block==null||block.isEqual(other.block));
        }
        return false;
    }
}