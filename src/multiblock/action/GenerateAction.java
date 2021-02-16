package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.overhaul.turbine.OverhaulTurbine;
public class GenerateAction extends Action<Multiblock>{
    private final Multiblock multiblock;
    private Block[][][] was;
    public GenerateAction(Multiblock multiblock){
        this.multiblock = multiblock;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        if(allowUndo)was = new Block[multiblock.getX()][multiblock.getY()][multiblock.getZ()];
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    if(allowUndo)was[x][y][z] = multiblock.getBlock(x, y, z);
                    Block block = this.multiblock.getBlock(x, y, z);
                    if(multiblock instanceof OverhaulTurbine&&block==null)continue;
                    multiblock.setBlock(x, y, z, block);
                }
            }
        }
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    multiblock.setBlockExact(x, y, z, was[x][y][z]);
                }
            }
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){
        //TODO only list the actually affected blocks
    }
}