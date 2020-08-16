package multiblock.action;
import java.util.ArrayList;
import java.util.HashMap;
import multiblock.Action;
import multiblock.overhaul.turbine.Block;
import multiblock.overhaul.turbine.OverhaulTurbine;
public class SetBearingDiameterAction extends Action<OverhaulTurbine>{
    private final int newDiameter;
    private int oldDiameter;
    private HashMap<int[], Block> was = new HashMap<>();
    public SetBearingDiameterAction(int newDiameter){
        this.newDiameter = newDiameter;
    }
    @Override
    protected void doApply(OverhaulTurbine multiblock){
        was.clear();
        oldDiameter = multiblock.bearingDiameter;
        multiblock.bearingDiameter = newDiameter;
        Block shaft = multiblock.getBlock(multiblock.getX()/2, multiblock.getY()/2, 1);
        Block bearing = multiblock.getBlock(multiblock.getX()/2, multiblock.getY()/2, 0);
        int minD = multiblock.getX()/2-Math.max(oldDiameter, newDiameter)/2;
        int maxD = multiblock.getX()-minD-1;
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    was.put(new int[]{x,y,z}, multiblock.getBlock(x, y, z));
                    if(x>=minD&&y>=minD&&x<=maxD&&y<=maxD)multiblock.setBlockExact(x, y, z, null);
                }
            }
        }
        minD = multiblock.getX()/2-newDiameter/2;
        maxD = multiblock.getX()-minD-1;
        Block[] blades = new Block[multiblock.getZ()-2];
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    if(x>=minD&&x<=maxD&&y>=minD&&y<=maxD){
                        if(z==0||z==multiblock.getZ()-1){
                            multiblock.setBlock(x, y, z, bearing);
                        }else{
                            multiblock.setBlockExact(x, y, z, shaft);
                        }
                    }else{
                        Block b = multiblock.getBlock(x, y, z);
                        if(z==0||z==multiblock.getZ()-1){
                            if(b!=null){
                                b.x = x;
                                b.y = y;
                                b.z = z;
                            }
                        }else{
                            if(b!=null&&blades[z-1]==null)blades[z-1] = b;
                        }
                    }
                }
            }
        }
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    if(!(x>=minD&&x<=maxD&&y>=minD&&y<=maxD)){
                        if(z!=0&&z!=multiblock.getZ()-1){
                            multiblock.setBlockExact(x, y, z, blades[z-1]);
                        }
                    }
                }
            }
        }
    }
    @Override
    protected void doUndo(OverhaulTurbine multiblock){
        multiblock.bearingDiameter = oldDiameter;
        int minD = multiblock.getX()/2-Math.max(oldDiameter, newDiameter)/2;
        int maxD = multiblock.getX()-minD-1;
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    if(x>=minD&&y>=minD&&x<=maxD&&y<=maxD)multiblock.setBlockExact(x, y, z, null);
                }
            }
        }
        for(int[] is : was.keySet()){
            multiblock.setBlockExact(is[0], is[1], is[2], was.get(is));
        }
    }
    @Override
    protected void getAffectedBlocks(OverhaulTurbine multiblock, ArrayList<multiblock.Block> blocks){
        blocks.addAll(multiblock.getBlocks());
    }
}