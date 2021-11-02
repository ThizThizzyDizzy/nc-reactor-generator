package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.Block;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
public class SetBladeAction extends Action<OverhaulTurbine>{
    public final int bearingSize;
    public final int z;
    public final Block block;
    private HashMap<BlockPos, Block> was = new HashMap<>();
    public SetBladeAction(int bearingSize, int z, Block block){
        this.bearingSize = bearingSize;
        this.z = z;
        this.block = block;
    }
    @Override
    public void doApply(OverhaulTurbine multiblock, boolean allowUndo){
        if(allowUndo){
            int bearingMax = multiblock.getExternalWidth()/2+bearingSize/2;
            int bearingMin = multiblock.getExternalWidth()/2-bearingSize/2;
            for(int x = 1; x<=multiblock.getInternalWidth(); x++){
                for(int y = 1; y<=multiblock.getInternalHeight(); y++){
                    boolean isXBlade = x>=bearingMin&&x<=bearingMax;
                    boolean isYBlade = y>=bearingMin&&y<=bearingMax;
                    if(isXBlade&&isYBlade)continue;//that's the bearing
                    if(isXBlade||isYBlade)was.put(new BlockPos(x, y, z), multiblock.getBlock(x, y, z));
                }
            }
        }
        multiblock.setBlade(bearingSize, z, block.template);
    }
    @Override
    public void doUndo(OverhaulTurbine multiblock){
        for(BlockPos pos : was.keySet()){
            multiblock.setBlockExact(pos.x, pos.y, pos.z, was.get(pos));
        }
    }
    @Override
    public void getAffectedBlocks(OverhaulTurbine multiblock, ArrayList blocks){}
    @Override
    public boolean equals(Object obj){
        if(obj instanceof SetBladeAction){
            SetBladeAction other = (SetBladeAction)obj;
            if(block==null&&other.block!=null)return false;
            return bearingSize==other.bearingSize&&z==other.z&&(block==null||block.isEqual(other.block));
        }
        return false;
    }
}